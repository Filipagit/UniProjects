# provas-microservice.py
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from datetime import datetime
from flask_cors import CORS


app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://probum:password@localhost:3308/Salasdb'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_CHARSET'] = 'utf8mb4'
app.config['SQLALCHEMY_DATABASE_COLLATION'] = 'utf8mb4_unicode_ci'

db = SQLAlchemy(app)
migrate = Migrate(app, db)

class Sala(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    sala = db.Column(db.String(100), nullable=False)
    edificio = db.Column(db.String(100), nullable=False)
    piso = db.Column(db.Integer , nullable=False)
    capacidade = db.Column(db.Integer, nullable=False)
    reservas = db.relationship('Reserva', backref='sala', lazy=True)


class Reserva(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    id_sala = db.Column(db.Integer,db.ForeignKey('sala.id') , nullable=False)
    id_responsavel = db.Column(db.Integer, nullable=False)
    data_reserva = db.Column(db.Date, nullable=False)
    hora_inicio = db.Column(db.Time, nullable=False)
    hora_fim = db.Column(db.Time, nullable=False)


# obter todas as salas
@app.route('/Probum/api/salas', methods=['GET'])
def get_salas():
    salas = Sala.query.all()

    salas_list = []

    for sala in salas:
        salas_list.append({
            'id': sala.id,
            'sala': sala.sala,
            'edificio': sala.edificio,
            'piso': sala.piso,
            'capacidade': sala.capacidade,
        })

    return jsonify({'salas': salas_list})


# obter uma sala
@app.route('/Probum/api/salas/<int:id>', methods=['GET'])
def get_sala(id):
    sala = Sala.query.first(id=id)

    if not sala:
        return jsonify({'message': 'Sala não encontrada'}), 404
    
    sala = {
        'id': sala.id,
        'sala': sala.sala,
        'edificio': sala.edificio,
        'piso': sala.piso,
        'capacidade': sala.capacidade,
        'reservas': [
            {
                'id': reserva.id,
                'id_sala': reserva.id_sala,
                'id_responsavel': reserva.id_responsavel,
                'data_reserva': reserva.data_reserva,
                'hora_inicio': reserva.hora_inicio,
                'hora_fim': reserva.hora_fim
            } for reserva in sala.reservas
        ]
    }

    return jsonify({'sala': sala})

# criar uma sala
@app.route('/Probum/api/sala', methods=['POST'])
def create_sala():
    data = request.get_json()

    sala = Sala(
        sala=data['sala'],
        edificio=data['edificio'],
        piso=data['piso'],
        capacidade=data['capacidade']
    )

    db.session.add(sala)
    db.session.commit()

    return jsonify({'message': 'Sala criada com sucesso'}), 201


# atualizar uma sala
@app.route('/Probum/api/sala/<int:id>', methods=['PUT'])
def update_sala(id):
    print(id)
    sala = Sala.query.filter_by(id=id).first()

    if not sala:
        return jsonify({'message': 'Sala não encontrada'}), 404

    data = request.get_json()

    sala.sala = data['sala']
    sala.edificio = data['edificio']
    sala.piso = data['piso']
    sala.capacidade = data['capacidade']

    db.session.commit()

    return jsonify({'message': 'Sala atualizada com sucesso'})


# eliminar uma sala
@app.route('/Probum/api/salas/<int:id>', methods=['DELETE'])
def delete_sala(id):
    sala = Sala.query.filter_by(id=id).first()

    if not sala:
        return jsonify({'message': 'Sala não encontrada'}), 404

    db.session.delete(sala)
    db.session.commit()

    return jsonify({'message': 'Sala deletada com sucesso'})

# obter as reservas de uma sala
@app.route('/Probum/api/salas/reservas/<int:id>', methods=['GET'])
def get_reservas(id):
    reservas = Reserva.query.filter_by(id=id).all()  # Correção: adicionar () após all

    reservas_list = {
        'reservas': [
            {
                'id': reserva.id,
                'id_sala': reserva.id_sala,
                'id_responsavel': reserva.id_responsavel,
                'data_reserva': reserva.data_reserva.strftime('%Y-%m-%d'), 
                'hora_inicio': reserva.hora_inicio.strftime('%H:%M:%S'),  
                'hora_fim': reserva.hora_fim.strftime('%H:%M:%S')  
            } for reserva in reservas
        ]
    }

    return jsonify(reservas_list)


# criar uma reserva de uma sala
@app.route('/Probum/api/salas/reservas', methods=['POST'])
def create_reserva():

    data = request.get_json()

    # Verificar se a sala existe
    sala = Sala.query.filter_by(id=data['id_sala']).first()
    print(sala)
    if not sala:
        return jsonify({'message': 'Sala não encontrada'}), 404
    
    # Converter a string '12:00:00' para um objeto datetime.time
    hora_inicio = datetime.strptime(data['hora_inicio'], '%H:%M:%S').time()
    hora_fim = datetime.strptime(data['hora_fim'], '%H:%M:%S').time()

    print(hora_inicio)

    # Criar a reserva
    nova_reserva = Reserva(
            id_sala=data['id_sala'],
            id_responsavel=data['id_responsavel'],
            data_reserva=datetime.strptime(data['data_reserva'], '%Y-%m-%d').date(),
            hora_inicio=datetime.strptime(data['hora_inicio'], '%H:%M:%S').time(),
            hora_fim=datetime.strptime(data['hora_fim'], '%H:%M:%S').time()
        )

    # Adicionar a reserva ao banco de dados
    db.session.add(nova_reserva)
    db.session.commit()

    return jsonify({'message': 'Reserva criada com sucesso'}), 201




# eliminar uma reserva de uma sala
@app.route('/Probum/api/salas/reservas/<int:id_reserva>', methods=['DELETE'])
def delete_reserva( id_reserva):
    reserva = Reserva.query.first(id=id_reserva)

    if not reserva:
        return jsonify({'message': 'Reserva não encontrada'}), 404

    db.session.delete(reserva)
    db.session.commit()

    return jsonify({'message': 'Reserva deletada com sucesso'})

# obter uma reserva de uma sala
@app.route('/Probum/api/salas/reservas/<int:id_reserva>', methods=['GET'])
def get_reserva(id, id_reserva):

    reserva = Reserva.query.filter_by(id=id_reserva).first()

    if not reserva:
        return jsonify({'message': 'Reserva não encontrada'}), 404

    reserva = {
        'id': reserva.id,
        'id_sala': reserva.id_sala,
        'id_responsavel': reserva.id_responsavel,
        'data_reserva': reserva.data_reserva.strftime('%Y-%m-%d'), 
        'hora_inicio': reserva.hora_inicio.strftime('%H:%M:%S'), 
        'hora_fim': reserva.hora_fim
    }

    return jsonify({'reserva': reserva})
    

if __name__ == '__main__':
    app.run(debug=True, port=5002)
