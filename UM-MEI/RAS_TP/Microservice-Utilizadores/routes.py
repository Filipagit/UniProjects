# routes.py
from flask import request, jsonify, session, g
from __main__ import app
from app import mysql
import decorators
import logging
import re
import bcrypt
import jwt


logging.basicConfig(level=logging.INFO)

# Route for changing password
@app.route('/change_password', endpoint='change_password', methods=['POST'])
@decorators.login_required
def change_password():
    try:
        data = request.get_json()
        username = g.username  # Get the username of the currently logged-in user
        old_password = data.get('old_password').encode('utf-8')
        new_password = data.get('new_password').encode('utf-8')

        # Retrieve the user's current hashed password from the database
        cur = mysql.connection.cursor()
        cur.execute("SELECT user_id, role, password FROM user WHERE username = %s", (username,))
        user = cur.fetchone()

        print(f'Username: {username}')
        print(f'Old Password: {old_password}')

        # Check if the old password is correct
        if user and bcrypt.checkpw(old_password, user['password'].encode('utf-8')):
            # Check if the new password is different from the old one
            if old_password == new_password:
                cur.close()
                return jsonify({'error': 'New password must be different from the old password'}), 400

            # Hash the new password
            hashed_password = bcrypt.hashpw(new_password, bcrypt.gensalt())

            # Update the password in the database
            cur.execute("UPDATE user SET password = %s WHERE username = %s", (hashed_password, username))
            mysql.connection.commit()
            cur.close()

            return jsonify({'message': 'Password changed successfully'}), 200
        else:
            cur.close()
            return jsonify({'error': 'Invalid old password or user ID'}), 401

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Route for getting information about the currently logged-in user
@app.route('/get_user_info', endpoint='get_user_info',  methods=['GET'])
@decorators.login_required
def get_user_info():
    try:
        return jsonify({ 'username': g.username, 'email': g.email, 'role': g.role}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# User registration endpoint
@app.route('/register', methods=['POST'])
def register():
    try:
        # Get user data from request
        data = request.get_json()
        username = data['username']
        password = data['password'].encode('utf-8')
        email = data['email']
        role = "estudante"

        # Check if the username or email already exists
        cur = mysql.connection.cursor()
        cur.execute("SELECT * FROM user WHERE username = %s OR email = %s", (username, email))
        existing_user = cur.fetchone()
        
        # Validate the email format using a regular expression
        email_pattern = re.compile(r'^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$')
        if not email_pattern.match(email):
            cur.close()
            return jsonify({'error': 'Invalid email format'}), 400

        if existing_user:
            cur.close()
            return jsonify({'error': 'Username or email already exists'}), 400

        # Hash the password
        hashed_password = bcrypt.hashpw(password, bcrypt.gensalt())

        # Create a cursor and execute the query
        cur.execute("INSERT INTO user (username, password, email, role) VALUES (%s, %s, %s, %s)", (username, hashed_password, email, role))
        mysql.connection.commit()
        cur.close()

        return jsonify({'message': 'User registered successfully'}), 201

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Admin registration endpoint
@app.route('/registerteacher', methods=['POST'])
@decorators.admin_required
def registerTeacher():
    try:
        # Get user data from request
        data = request.get_json()
        username = data['username']
        password = data['password'].encode('utf-8')
        email = data['email']
        role = "docente"

        # Check if the username or email already exists
        cur = mysql.connection.cursor()
        cur.execute("SELECT * FROM user WHERE username = %s OR email = %s", (username, email))
        existing_user = cur.fetchone()

        # Validate the email format using a regular expression
        email_pattern = re.compile(r'^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$')
        if not email_pattern.match(email):
            cur.close()
            return jsonify({'error': 'Invalid email format'}), 400

        if existing_user:
            cur.close()
            return jsonify({'error': 'Username or email already exists'}), 400

        # Hash the password
        hashed_password = bcrypt.hashpw(password, bcrypt.gensalt())

        # Create a cursor and execute the query
        cur.execute("INSERT INTO user (username, password, email, role) VALUES (%s, %s, %s, %s)", (username, hashed_password, email, role))
        mysql.connection.commit()
        cur.close()

        return jsonify({'message': 'User registered successfully'}), 201

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# User login endpoint with token response
@app.route('/login', methods=['POST'])
def login():
    try:
        print (app.config['SECRET_KEY'])
        data = request.get_json()
        username = data['username']
        password = data['password'].encode('utf-8')

        # Create a cursor and execute the query
        cur = mysql.connection.cursor()
        cur.execute("SELECT * FROM user WHERE username = %s", (username,))
        user = cur.fetchone()
        cur.close()

        # Check if the user exists and the password is correct
        if user and bcrypt.checkpw(password, user['password'].encode('utf-8')):
            # Generate a JWT token with user information and role
            token_payload = {
                'user_id': user['user_id'],
                'username': user['username'],
                'email': user['email'],
                'role': user['role']
            }
            jwt_token = jwt.encode(token_payload, app.config['SECRET_KEY'], algorithm='HS256')

            return jsonify({'message': 'Login successful', 'token': jwt_token}), 200
        else:
            return jsonify({'message': 'Invalid username or password'}), 401

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Route for getting all docentes
@app.route('/get_docentes', methods=['GET'])
@decorators.teacher_required
def get_docentes():
    try:
        # Create a cursor and execute the query
        cur = mysql.connection.cursor()
        cur.execute("SELECT user_id, username, email FROM user WHERE role = 'docente'")
        docentes = cur.fetchall()
        cur.close()

        # Return the list of docentes in JSON format
        return jsonify({'docentes': docentes}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Logout endpoint
@app.route('/logout', methods=['GET'])
def logout():
    # Clear the session
    session.clear()
    return jsonify({'message': 'Logout successful'}), 200
