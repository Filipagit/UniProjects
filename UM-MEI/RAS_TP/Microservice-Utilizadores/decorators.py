# decorators.py
from flask import request, jsonify, g
import jwt
from __main__ import app

# Custom decorator to check if the user has the "estudante" role
def student_required(view_func):
    def is_student(*args, **kwargs):
        token = request.headers.get('Authorization')
        token = token.replace("Bearer ", "")
        if not token:
            return jsonify({'error': 'Token is missing'}), 401

        try:
            print (app.config['SECRET_KEY'])
            payload = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            g.user_id = payload['user_id']
            g.username = payload['username']
            g.role = payload['role']

            if g.role != 'estudante':
                return jsonify({'error': 'Student privileges required'}), 403

        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid token'}), 401

        return view_func(*args, **kwargs)

    return is_student

# Custom decorator to check if the user has the "docente" role
def teacher_required(view_func):
    def is_teacher(*args, **kwargs):
        token = request.headers.get('Authorization')
        token = token.replace("Bearer ", "")
        if not token:
            return jsonify({'error': 'Token is missing'}), 401

        try:
            payload = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            g.user_id = payload['user_id']
            g.username = payload['username']
            g.role = payload['role']

            if g.role != 'docente':
                return jsonify({'error': 'Teacher privileges required'}), 403

        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid token'}), 401

        return view_func(*args, **kwargs)

    return is_teacher

# Custom decorator to check if the user has the "admin" role
def admin_required(view_func):
    def is_admin(*args, **kwargs):
        token = request.headers.get('Authorization')
        token = token.replace("Bearer ", "")
        if not token:
            return jsonify({'error': 'Token is missing'}), 401

        try:
            payload = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            g.user_id = payload['user_id']
            g.username = payload['username']
            g.role = payload['role']
            g.email = payload['email']
            if g.role != 'admin':
                return jsonify({'error': 'Admin privileges required'}), 403

        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid token'}), 401

        return view_func(*args, **kwargs)

    return is_admin

# Custom decorator to check if the user is authenticated
def login_required(view_func):
    def is_logged(*args, **kwargs):
        token = request.headers.get('Authorization')
        token = token.replace("Bearer ", "")
        if not token:
            return jsonify({'error': 'Token is missing'}), 401

        try:
            payload = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            g.user_id = payload['user_id']
            g.username = payload['username']
            g.email = payload['email']
            g.role = payload['role']

        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token has expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid token'}), 401

        return view_func(*args, **kwargs)

    return is_logged