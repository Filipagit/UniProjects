# provas-microservice.py
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
import pandas as pd
from datetime import datetime, timedelta
from sqlalchemy import func,and_
from flask_cors import CORS


# Apenas para DEBUG enquanto não temos o microserviço de contas
dict_type = {1: 'aluno', 2: 'professor', 3: 'tecnico'}  # dicionario de tipos de conta
dict = {'joao': '123', 'maria': '123', 'pedro': '123'}  # dicionario de contas
dict_id = {'joao': 1, 'maria': 2, 'pedro': 3}  # dicionario de contas

# CONTAS_MICROSERVICE_URL = 'http://localhost:5000' # ver a porta do microservico de contas

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://probum:password@localhost/Provasdb'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_CHARSET'] = 'utf8mb4'
app.config['SQLALCHEMY_DATABASE_COLLATION'] = 'utf8mb4_unicode_ci'

db = SQLAlchemy(app)
migrate = Migrate(app, db)

class Prova(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    nome = db.Column(db.String(100), nullable=False)
    uc = db.Column(db.String(100), nullable=False)
    alunos = db.relationship('AlunoProva', backref='prova', lazy=True)
    versoes = db.relationship('VersaoProva', backref='prova', lazy=True)
    tempo_admissao = db.Column(db.Integer, nullable=False)
    duracao = db.Column(db.Integer, nullable=False)
    criador_id = db.Column(db.Integer, nullable=False)
    avaliada = db.Column(db.Boolean, default=False, nullable=False) # para saber se a prova ja foi avaliada pelo docente

class VersaoProva(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    numero = db.Column(db.Integer, nullable=False)
    sala_id = db.Column(db.Integer, nullable=False) 
    data = db.Column(db.DATETIME, nullable=False)
    prova_id = db.Column(db.Integer, db.ForeignKey('prova.id'), nullable=False)
    questoes = db.relationship('QuestaoVersao', backref='versao', lazy=True)


class QuestaoVersao(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    pergunta = db.Column(db.String(200), nullable=False)
    versao_id = db.Column(db.Integer, db.ForeignKey('versao_prova.id'), nullable=False)
    opcoes = db.relationship('Opcao', backref='questao_versao', lazy=True)
    tag = db.Column(db.String(200), nullable=False)
    pontuacao = db.Column(db.Integer, nullable=False)

class Opcao(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    texto = db.Column(db.String(200), nullable=False)
    correta = db.Column(db.Boolean, nullable=False)
    questao_id = db.Column(db.Integer, db.ForeignKey('questao_versao.id'), nullable=False)


class RespostaAluno(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    aluno_prova_id = db.Column(db.Integer, db.ForeignKey('aluno_prova.id'), nullable=False)
    questao_versao_id = db.Column(db.Integer, db.ForeignKey('questao_versao.id'), nullable=False)
    opcao_id = db.Column(db.Integer, db.ForeignKey('opcao.id'), nullable=True)
    resposta_texto = db.Column(db.String(200), nullable=True)

class AlunoProva(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    aluno_id = db.Column(db.Integer, nullable=False)
    prova_id = db.Column(db.Integer, db.ForeignKey('prova.id'), nullable=False)
    versao_id = db.Column(db.Integer, db.ForeignKey('versao_prova.id'), nullable=True)
    resultado = db.Column(db.Integer, nullable=True)
    respostas = db.relationship('RespostaAluno', backref='aluno_prova', lazy=True)
    disp_resultado = db.Column(db.Boolean, default=False, nullable=False) 


# Endpoint para obter todas as provas apenas para DEBUG:
@app.route('/Probum/api/provas', methods=['GET'])
def getAllProvas():
    all_provas = Prova.query.all()
    provas_list = []

    for prova in all_provas:
        provas_list.append({
            'id': prova.id,
            'nome': prova.nome,
            'uc' : prova.uc,
            'tempo_admissao': prova.tempo_admissao,
            'duracao': prova.duracao,
            'criador_id': prova.criador_id,
            'avalida': prova.avaliada
        })

    return jsonify(provas_list), 200

@app.route('/Probum/api/provas/<int:criador_id>', methods=['GET'])
def pedidoListaProvas(criador_id):
    provas = Prova.query.filter_by(criador_id=criador_id).all()

    if not provas:
        return jsonify({'message': 'Nenhuma prova encontrada para o criador especificado'}), 404

    provas_list = []

    for prova in provas:
        provas_list.append({
            'id': prova.id,
            'nome': prova.nome,
            'uc': prova.uc,
            'tempo_admissao': prova.tempo_admissao,
            'duracao': prova.duracao,
        })

    return jsonify(provas_list), 200

# Endpoint para obter provas associadas a um criador
@app.route('/Probum/api/provas/<int:criador_id>/<int:prova_id>', methods=['GET'])
def pedidoDetalhesProva(criador_id,prova_id):
    print(f"criador_id: {criador_id}, prova_id: {prova_id}")

    prova = Prova.query.filter_by(criador_id=criador_id, id=prova_id).first()

    if not prova:
        return jsonify({'message': 'Nenhuma prova encontrada para o criador especificado'}), 404

    prova_data = {
        'id': prova.id,
        'nome': prova.nome,
        'uc': prova.uc,
        'tempo_admissao': prova.tempo_admissao,
        'duracao': prova.duracao,
        'criador_id': prova.criador_id,
        'alunos': [
            {
                'id': aluno_prova.id,
                'aluno_id': aluno_prova.aluno_id,
                'versao_id': aluno_prova.versao_id
            } for aluno_prova in prova.alunos
        ],
        'versoes': [
            {
                'id': versao.id,
                'numero': versao.numero,
                'sala_id': versao.sala_id,
                'data': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
                'prova_id': versao.prova_id,
                'questoes': [
                    {
                        'id': questao.id,
                        'pergunta': questao.pergunta,
                        'tag': questao.tag,
                        'versao_id': questao.versao_id,
                        'opcoes': [
                            {
                                'id': opcao.id,
                                'texto': opcao.texto,
                                'correta': opcao.correta
                            } for opcao in questao.opcoes
                        ]
                    } for questao in versao.questoes
                ]
            } for versao in prova.versoes
        ]
    }

    return jsonify(prova_data), 200

@app.route('/Probum/api/checkInfProva', methods=['POST'])
def checkInfProva():
    try:
        nome_prova = request.form['nomeProva']
        arquivo_csv = request.files['listaAlunos']

        df = pd.read_csv(arquivo_csv)
        
        # print(df.head())
        # verificar se os alunos estao registados na dase de dados de contas
        # response = requests.post(
        #     f'{CONTAS_MICROSERVICE_URL}/api/check_inf_alunos',
        #     data=df,
        #     headers={'Content-Type': 'text/csv'}
        # )
        # if response.status_code == 200:
        #     alunos_registrados = response.json().get("alunos_registrados", [])
        #     return jsonify({"status": "OK", "mensagem": f"Informações da prova '{nome_prova}' verificadas com sucesso."})
        # else:
        #     return jsonify({"status": "Erro", "mensagem": f"Erro ao verificar informações dos alunos. Detalhes: {response.json()}"}), 500
    
        ##testar enquanto nao temos o microservico de contas
        print(df.head())
        print(df['Nome'])
        if all(nome in dict for nome in df['Nome']):
            print('ok')
            return jsonify({"status": "OK", "mensagem": f"Informações da prova '{nome_prova}' verificadas com sucesso."})
        else:
            return jsonify({"status": "Erro", "mensagem": f"Erro ao verificar informações dos alunos."}), 500
        
    except Exception as e:
        return jsonify({"status": "Erro", "mensagem": str(e)})
    

# Endpoint para criar uma nova prova
@app.route('/Probum/api/provas', methods=['POST'])
def criar_prova():
    data = request.get_json()

    if not data or 'alunos_ids' not in data:
        return jsonify({'message': 'Dados inválidos ou nenhum aluno fornecido'}), 400

    nova_prova = Prova(
        nome=data['nome'],
        uc=data['uc'],
        tempo_admissao=data['tempo_admissao'],
        duracao=data['duracao'],
        criador_id=data['criador_id']
    )

    db.session.add(nova_prova)
    db.session.commit()

    alunos_ids = data['alunos_ids']

    for aluno_id in alunos_ids:
        aluno_prova_association = AlunoProva(
            aluno_id=aluno_id,
            prova_id=nova_prova.id,
            resultado=0
        )
        db.session.add(aluno_prova_association)

    db.session.commit()

    return jsonify({'message': 'Prova criada com sucesso', 'id': nova_prova.id}), 201


def distribuir_alunos(num_alunos, capacidades):
    salas = {sala_id: 0 for sala_id in capacidades.keys()}

    for sala_id in sorted(salas, key=lambda x: capacidades[x]):
        if num_alunos == 0:
            break
        disponivel = capacidades[sala_id]
        alunos_distribuidos = min(num_alunos, disponivel)
        salas[sala_id] += alunos_distribuidos
        num_alunos -= alunos_distribuidos

    for sala_id, alunos in salas.items():
        print(f"{sala_id}: {alunos} aluno(s)")

    return salas


# Endpoint para criar versões com base nos IDs de salas
@app.route('/Probum/api/criarVersoes', methods=['POST'])
def criar_versoes():
    data = request.get_json()
    id_prova = data['id_prova']
    salas = data['salas']
    
    prova = db.session.get(Prova, id_prova)

    if prova is None:
        return jsonify({"error": f"Prova com id {id_prova} não nao existe."}), 400
    
    if prova.criador_id != data['criador_id']:
        return jsonify({"error": f"Prova com id {id_prova} não pertence ao criador especificado."}), 400    
    
    num_alunos = len(prova.alunos)

    capacidades = {sala["id_sala"]: sala["lotacao"] for sala in salas}
    dist = distribuir_alunos(num_alunos, capacidades)

    versoes = []
    aux = 0
    count = 0
    for sala_id, lotacao in dist.items():
        sala = next((sala for sala in salas if sala["id_sala"] == sala_id), None)

        if sala is None:
            return jsonify({"error": f"Sala com id {sala_id} não encontrada."}), 400
        hora_recebida = sala["hora"]
        data_recebida = sala["data"]

        data_formatada = datetime.strptime(f'{data_recebida} {hora_recebida}', '%Y-%m-%d %H:%M:%S')

        versao = VersaoProva(
            numero=aux,
            sala_id=sala_id,
            data=data_formatada,
            prova_id=id_prova
        )
        aux += 1
        db.session.add(versao)
        db.session.commit()

        # Adiciona a versão à lista de versões
        versoes.append(versao.id)

        for i in range(lotacao):
            aluno_prova = AlunoProva.query.filter_by(aluno_id=prova.alunos[count].aluno_id, prova_id=id_prova).first()

            if aluno_prova:
                aluno_prova.versao_id = versao.id

            count += 1
            db.session.commit()

    return jsonify({"versoes": versoes}), 201


@app.route('/Probum/api/criarQuestao', methods=['POST'])
def criar_questao():
    data = request.get_json()

    if 'versao_id' not in data or 'pergunta' not in data or 'opcoes' not in data:
        return jsonify({'message': 'Campos obrigatórios ausentes'}), 400

    # Criar nova questão
    nova_questao = QuestaoVersao(
        versao_id=data['versao_id'],
        pergunta=data['pergunta'],
        tag=data['tag'],
        pontuacao=data['pontuacao']
    )

    # adicionar questao à base de dados
    db.session.add(nova_questao)
    db.session.commit()

    # Adicionar opções à questão
    for opcao_info in data['opcoes']:
        nova_opcao = Opcao(
            texto=opcao_info['texto'],
            correta=bool(opcao_info['correta']),
            questao_id=nova_questao.id
        )
        db.session.add(nova_opcao)

    db.session.commit()

    return jsonify({'message': 'Questão criada com sucesso'}), 201


@app.route('/Probum/api/editarProva/<int:criador_id>', methods=['POST'])
def editar_detalhes_prova(criador_id):
    data = request.get_json()

    if not data or 'id' not in data:
        return jsonify({'message': 'Dados inválidos ou nenhum ID fornecido'}), 400

    prova = Prova.query.filter_by(id=data['id'],criador_id=criador_id).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404

    prova.nome = data['nome']
    prova.uc = data['uc']
    prova.tempo_admissao = data['tempo_admissao']
    prova.duracao = data['duracao']

    #TODO: VER MANEIRA DE EDITAR SALAS , TALVEZ APAGAR AS VERSOES E CRIAR NOVAS
    # MAS PRIMEIRO TEM QUE VER SE AS SALAS ESTAO LIVRES PARA A DATA E HORA, NO OUTRO MICROSERVICO

    db.session.commit()

    return jsonify({'message': 'Prova editada com sucesso'}), 200

@app.route('/Probum/api/questao/<int:versao_id>', methods=['GET'])
def getQuestoes_versao(versao_id):
    questoes = QuestaoVersao.query.filter_by(versao_id=versao_id).all()

    if not questoes:
        return jsonify({'message': 'Questoes não encontradas'}), 404
    
    questoes_list = []

    for questao in questoes:
        questoes_list.append({
            'id': questao.id,
            'pergunta': questao.pergunta,
            'tag': questao.tag,
            'versao_id': questao.versao_id,
            'pontuacao': questao.pontuacao,
            'opcoes': [
                {
                    'id': opcao.id,
                    'texto': opcao.texto,
                    'correta': opcao.correta
                } for opcao in questao.opcoes
            ]
        })

    return jsonify(questoes_list), 200

@app.route('/Probum/api/questao/<int:questao_id>', methods=['POST'])
def editarQuestao(questao_id):
    data = request.get_json()

    if not data or 'id' not in data:
        return jsonify({'message': 'Dados inválidos ou nenhum ID fornecido'}), 400

    questao = QuestaoVersao.query.filter_by(id=questao_id).first()

    if not questao:
        return jsonify({'message': 'Questao não encontrada'}), 404
    if data['pergunta']:
        questao.pergunta = data['pergunta']

    if data['tag']:
        questao.tag = data['tag']

    if data['pontuacao']:
        questao.pontuacao = data['pontuacao']
    
    if data['opcoes']:
        for opcao_info in data['opcoes']:
            opcao = Opcao.query.filter_by(id=opcao_info['id']).first()
            if opcao:
                if opcao_info['texto']:
                    opcao.texto = opcao_info['texto']
                if opcao_info['correta']:
                    opcao.correta = opcao_info['correta']

                db.session.commit()
            else:
                return jsonify({'message': 'Opcao não encontrada'}), 404
    
    db.session.commit()

    return jsonify({'message': 'Questao editada com sucesso'}), 200

@app.route('/Probum/api/questao/<int:questao_id>', methods=['DELETE'])
def apagarQuestao(questao_id):

    questao = QuestaoVersao.query.filter_by(id=questao_id).first()

    if not questao:
        return jsonify({'message': 'Questao não encontrada'}), 404
    
    for opcao in questao.opcoes:
        db.session.delete(opcao)

    db.session.delete(questao)
    db.session.commit()

    return jsonify({'message': 'Questao apagada com sucesso'}), 200


@app.route('/Probum/api/provasAtuais/<int:aluno_id>', methods=['GET'])
def obterProvasAtuais(aluno_id):
    # Obter a data atual
    data_atual = datetime.now()

    print(f"Data atual: {data_atual}")

    provas_atuais = (
    db.session.query(Prova, VersaoProva)
    .join(VersaoProva, Prova.id == VersaoProva.prova_id)
    .join(AlunoProva, AlunoProva.prova_id == Prova.id)
    .filter(
        AlunoProva.aluno_id == aluno_id,
        AlunoProva.versao_id == VersaoProva.id,
        func.DATE(VersaoProva.data) == func.DATE(data_atual),
        (func.EXTRACT('hour', VersaoProva.data) * 60 + func.EXTRACT('minute', VersaoProva.data)) <= func.EXTRACT('hour', data_atual) * 60 + func.EXTRACT('minute', data_atual),
        (func.EXTRACT('hour', VersaoProva.data) * 60 + func.EXTRACT('minute', VersaoProva.data) + Prova.tempo_admissao) >= func.EXTRACT('hour', data_atual) * 60 + func.EXTRACT('minute', data_atual)
    )
    .all()
    )   


    # Formatar os resultados da consulta
    json = []
    for prova, versao in provas_atuais:
        json.append({
            'prova_id': prova.id,
            'nome_prova': prova.nome,
            'uc': prova.uc,
            'duracao': prova.duracao,
            'admissao': prova.tempo_admissao,
            'versao_id': versao.id,
            'data_versao': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
        })

    return jsonify({'provas_atuais': json})


@app.route('/Probum/api/detalhesProva/<int:prova_id>/<int:versao_id>', methods=['GET'])
def detalhesProva(prova_id,versao_id):

    prova = Prova.query.filter_by(id=prova_id).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404
    
    versao = VersaoProva.query.filter_by(id=versao_id).first()

    if not versao:
        return jsonify({'message': 'Versão não encontrada'}), 404

    num_incritos = len(prova.alunos)

    json = {
        'nome': prova.nome,
        'uc': prova.uc,
        'data': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
        'duracao': prova.duracao,
        'num_inscritos': num_incritos
    }

    return jsonify(json)

# verifica se esta no tempo de admissao e devolve a primeira questao
# e manda uma lista com os ids das questoes todas da prova para depois pedir as seguintes
@app.route('/Probum/api/realizarProva/<int:prova_id>/<int:versao_id>', methods=['GET']) 
def realizarProva(prova_id,versao_id):
    
        prova = Prova.query.filter_by(id=prova_id).first()
    
        if not prova:
            return jsonify({'message': 'Prova não encontrada'}), 404
        
        versao = VersaoProva.query.filter_by(id=versao_id).first()
    
        if not versao:
            return jsonify({'message': 'Versão não encontrada'}), 404
        
        data_atual = datetime.now()
        if data_atual > versao.data + timedelta(minutes=prova.tempo_admissao):
            return jsonify({'message': 'Tempo de admissão expirado'}), 404
    
        questao = QuestaoVersao.query.filter_by(versao_id=versao_id).order_by(QuestaoVersao.id).first()

        if not questao:
            return jsonify({'message': 'Questões não encontradas'}), 404
        

        questoes = QuestaoVersao.query.filter_by(versao_id=versao_id).order_by(QuestaoVersao.id).all()

        lista_questoes = []

        for questao in questoes:
            lista_questoes.append(questao.id)

        opcoes = Opcao.query.filter_by(questao_id=questao.id).all()

        json = {
            'nome': prova.nome,
            'data': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
            'duracao': prova.duracao,
            'lista_questoes': lista_questoes,
            'questao': {
                'id': questao.id,
                'pergunta': questao.pergunta,
                'tag': questao.tag,
                'opcoes': [
                    {
                        'id': opcao.id,
                        'texto': opcao.texto,
                    } for opcao in opcoes
                ]
            }
        }
    
        return jsonify(json)

@app.route('/Probum/api/getQuestaoRealizar/<int:prova_id>/<int:versao_id>/<int:questao_id>', methods=['GET']) 
def getQuestaoRealizar(prova_id,versao_id,questao_id):
        
        prova = Prova.query.filter_by(id=prova_id).first()

        if not prova:
            return jsonify({'message': 'Prova não encontrada'}), 404
            
        versao = VersaoProva.query.filter_by(id=versao_id).first()
    
        if not versao:
            return jsonify({'message': 'Versão não encontrada'}), 404
        
        data_atual = datetime.now()
        if data_atual > versao.data + timedelta(minutes=prova.duracao):
            return jsonify({'message': 'Tempo de prova expirado'}), 404

        questao = QuestaoVersao.query.filter_by(versao_id=versao_id,id=questao_id).first()

        if not questao:
            return jsonify({'message': 'Questões não encontradas'}), 404
        
        opcoes = Opcao.query.filter_by(questao_id=questao.id).all()

        json = {
            'nome': prova.nome,
            'data': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
            'duracao': prova.duracao,
            'questao': {
                'id': questao.id,
                'pergunta': questao.pergunta,
                'tag': questao.tag,
                'opcoes': [
                    {
                        'id': opcao.id,
                        'texto': opcao.texto,
                    } for opcao in opcoes
                ]
            }
        }
    
        return jsonify(json)


@app.route('/Probum/api/realizar_prova', methods=['POST'])
def submeterRespostaQuestao():
    data = request.get_json()

    if not data or 'aluno_id' not in data or 'opcao_id' not in data or 'resposta' not in data:
        return jsonify({'message': 'Dados inválidos ou nenhum ID fornecido'}), 400

    prova = Prova.query.filter_by(id=data['prova_id']).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404
        
    versao = VersaoProva.query.filter_by(id=data['versao_id']).first()

    if not versao:
        return jsonify({'message': 'Versão não encontrada'}), 404
    
    data_atual = datetime.now()
    if data_atual > versao.data + timedelta(minutes=prova.duracao):
        return jsonify({'message': 'Tempo de prova expirado'}), 404

    questao = QuestaoVersao.query.filter_by(id=data['questao_id']).first()

    if not questao:
        return jsonify({'message': 'Questão não encontrada'}), 404

    aluno = AlunoProva.query.filter_by(aluno_id=data['aluno_id'], prova_id=data['prova_id']).first()

    if not aluno:
        return jsonify({'message': 'Aluno não encontrado'}), 404

    resposta = RespostaAluno.query.filter_by(aluno_prova_id=aluno.id, questao_versao_id=questao.id).first()

    if not resposta:
        resposta = RespostaAluno(
            aluno_prova_id=aluno.id,
            questao_versao_id=questao.id,
            opcao_id=data['opcao_id'],
            resposta_texto=data['resposta']
        )
        db.session.add(resposta)
    else:
        resposta.opcao_id = data['opcao_id']
        resposta.resposta_texto = data['resposta']

    db.session.commit()

    return jsonify({'message': 'Resposta submetida com sucesso'}), 200

#DEBUG: na parte de responder a prova, nao percebi o que iria fazer o finalizar prova, por isso nem o fiz  

@app.route('/Probum/api/listaProvasCorrigir/<int:criador_id>', methods=['GET'])
def listaProvasCorrigir(criador_id):
    data_atual = datetime.now()

    print(f"Data atual: {data_atual}")

    provas = (
    db.session.query(Prova, VersaoProva)
    .join(VersaoProva, Prova.id == VersaoProva.prova_id)
    .join(AlunoProva, AlunoProva.prova_id == Prova.id)
    .filter(
        Prova.criador_id == criador_id,
        Prova.avaliada == False,
        func.EXTRACT('hour', data_atual) * 60 + func.EXTRACT('minute', data_atual) >= (func.EXTRACT('hour', VersaoProva.data) * 60 + func.EXTRACT('minute', VersaoProva.data) + Prova.duracao)
    )
    .all()
    )    

    # print(provas)

    prova_counts = {}

    for prova, _ in provas:# isto e para so aparecer ao docente quando todas as versoes da prova estiverem realizadas
        if prova in prova_counts:
            prova_counts[prova] += 1
        else:
            prova_counts[prova] = 1
    
    # print(prova_counts)

    provasDisponiveis = []

    for prova, count in prova_counts.items():
        p = Prova.query.filter_by(id=prova.id).first()
        if(count == len(p.versoes)):
            provasDisponiveis.append(prova)

    # print(provasDisponiveis)

    json = []
    for prova in provasDisponiveis:
        json.append({
            'prova_id': prova.id,
            'nome_prova': prova.nome,
            'uc': prova.uc,
        })

    return jsonify({'provas_atuais': json}), 200

# Endpoint para corrigir automaticamente uma prova
@app.route('/Probum/api/corrigirProva/<int:prova_id>', methods=['POST'])
def corrigirProva(prova_id):
    data = request.get_json()

    if not data:
        return jsonify({'message': 'Dados inválidos ou nenhum ID fornecido'}), 400

    prova = Prova.query.filter_by(id=prova_id).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404

    if prova.criador_id != data['criador_id']:
        return jsonify({'message': 'Prova não pertence ao criador especificado'}), 404

    versoes = VersaoProva.query.filter_by(prova_id=prova_id).all()

    if not versoes:
        return jsonify({'message': 'Versões não encontradas'}), 404

    for versao in versoes:
        alunos = AlunoProva.query.filter_by(versao_id=versao.id).all()

        if not alunos:
            return jsonify({'message': 'Alunos não encontrados'}), 404

        for aluno in alunos:
            respostas = RespostaAluno.query.filter_by(aluno_prova_id=aluno.id).all()

            if not respostas:
                aluno.resultado = 0
            else:
                nota = 0
                for resposta in respostas:
                    if resposta.opcao_id:
                        opcao = Opcao.query.filter_by(id=resposta.opcao_id).first()
                        if opcao.correta:
                            questao = QuestaoVersao.query.filter_by(id=resposta.questao_versao_id).first()
                            nota += questao.pontuacao

                aluno.resultado = nota # TODO: VER SE AQUI FICA A SOMA DE TODAS AS QUESTOES OU SE FICA DE 0 A 20

    prova.avaliada = True

    db.session.commit()

    return jsonify({'message': 'Prova corrigida com sucesso'}), 200

# Endpoint para consultar lista de provas corrigidas para partilhar as notas com os alunos
@app.route('/Probum/api/listaProvasCorrigidas/<int:criador_id>', methods=['GET'])
def listaProvasCorrigidas(criador_id):
    provas = (
    db.session.query(Prova)
    .filter(
        Prova.criador_id == criador_id,
        Prova.avaliada,
        Prova.alunos.any(and_(
            ~AlunoProva.disp_resultado
        ))
    )
    .all()
    )

    json = []
    for prova in provas:
        json.append({
            'prova_id': prova.id,
            'nome_prova': prova.nome,
            'uc': prova.uc
        })

    return jsonify({'provas_atuais': json}), 200



# Endpoint para publicar as notas de uma prova
@app.route('/Probum/api/publicarNotas/<int:prova_id>', methods=['POST'])
def publicarNotas(prova_id):
    data = request.get_json()

    if not data:
        return jsonify({'message': 'Dados inválidos ou nenhum ID fornecido'}), 400

    prova = Prova.query.filter_by(id=prova_id).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404

    if prova.criador_id != data['criador_id']:
        return jsonify({'message': 'Prova não pertence ao criador especificado'}), 404

    versoes = VersaoProva.query.filter_by(prova_id=prova_id).all()

    if not versoes:
        return jsonify({'message': 'Versões não encontradas'}), 404

    for versao in versoes:
        alunos = AlunoProva.query.filter_by(versao_id=versao.id).all()

        if not alunos:
            return jsonify({'message': 'Alunos não encontrados'}), 404

        for aluno in alunos:
            aluno.disp_resultado = True

    db.session.commit()

    return jsonify({'message': 'Notas publicadas com sucesso'}), 200


# Endpoint para consultar lista de provas corrigidas e publicadas de um aluno
@app.route('/Probum/api/listaProvasPublicadas/<int:aluno_id>', methods=['GET'])
def listaProvasPublicadas(aluno_id):
    provas = (
        db.session.query(Prova, VersaoProva)
        .join(VersaoProva, Prova.id == VersaoProva.prova_id)
        .join(AlunoProva, AlunoProva.prova_id == Prova.id)
        .filter(
            AlunoProva.aluno_id == aluno_id,
            AlunoProva.versao_id == VersaoProva.id,
            AlunoProva.disp_resultado == True,
            Prova.avaliada == True
        )
        .all()
    )

    json = []
    for prova, versao in provas:
        json.append({
            'prova_id': prova.id,
            'nome_prova': prova.nome,
            'uc': prova.uc,
            'versao_numero': versao.numero,
            'versao_id': versao.id,
            'data_versao': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
            'resultado': AlunoProva.query.filter_by(aluno_id=aluno_id, prova_id=prova.id).first().resultado
        })

    return jsonify({'provas_atuais': json}), 200

# Endpoint para consultar detalhes de uma prova corrigida e publicada de um aluno
@app.route('/Probum/api/detalhesProvaCorrigida/<int:aluno_id>/<int:prova_id>/<int:versao_id>', methods=['GET'])
def detalhesProvaCorrigida(aluno_id, prova_id,versao_id):
    prova = Prova.query.filter_by(id=prova_id).first()

    if not prova:
        return jsonify({'message': 'Prova não encontrada'}), 404

    versao = VersaoProva.query.filter_by(id=versao_id).first()

    if not versao:
        return jsonify({'message': 'Versão não encontrada'}), 404

    aluno = AlunoProva.query.filter_by(aluno_id=aluno_id, prova_id=prova_id).first()

    if not aluno:
        return jsonify({'message': 'Aluno não encontrado'}), 404

    questoes = QuestaoVersao.query.filter_by(versao_id=versao.id).all()

    if not questoes:
        return jsonify({'message': 'Questões não encontradas'}), 404

    respostas = RespostaAluno.query.filter_by(aluno_prova_id=aluno.id).all()

    if not respostas:
        return jsonify({'message': 'Respostas não encontradas'}), 404
    
    json = {
        'nome': prova.nome,
        'data': versao.data.strftime('%Y-%m-%d %H:%M:%S'),
        'duracao': prova.duracao,
        'resultado': aluno.resultado,
        'questoes': [
            {
                'id': questao.id,
                'pergunta': questao.pergunta,
                'tag': questao.tag,
                'opcoes': [
                    {
                        'id': opcao.id,
                        'texto': opcao.texto,
                        'correta': opcao.correta
                    } for opcao in questao.opcoes
                ],
                'resposta': [
                    {
                        'id': resposta.id,
                        'opcao_id': resposta.opcao_id,
                        'resposta_texto': resposta.resposta_texto
                    } for resposta in respostas if resposta.questao_versao_id == questao.id
                ]
            } for questao in questoes
        ]
    }

    return jsonify(json), 200




if __name__ == '__main__':
    app.run(debug=True, port=5001)
