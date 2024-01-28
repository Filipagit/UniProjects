### 1º RODAR UM CONTAINER COM A BASE DE DADOS MYSQL 

```
docker run --name Salasdb -p 3308:3306 -d -e MYSQL_USER=probum -e MYSQL_PASSWORD=password -e MYSQL_DATABASE=Salasdb -e MYSQL_ALLOW_EMPTY_PASSWORD=true mysql:latest
```

### 2º INSTALAR AS DEPENDENCIAS DO PROJETO

```
pip install -r requirements.txt
```
### 3º Ir para a diretoria do projeto

```
cd Salas
```

### 4º Colocar as variaveis de ambiente para o flask

```
Para windows na powershell:
  $env:FLASK_APP = "salas-microservice.py" 
Para bash:
   export FLASK_APP=salas-microservice.py
```

### 5º Iniciar as migrações para a base de dados

```
python3 -m flask db init
```

### 6º Criar a migração para a base de dados

```
python3 -m flask db migrate -m "migrate1.0"
```

### 7º Aplicar a migração para a base de dados

```
python3 -m flask db upgrade
```

### 8º Iniciar o microserviço

```
python3 salas-microservice.py 
```

### 9º Link da documentação da API

```
https://documenter.getpostman.com/view/20678947/2s9YsGjZQ3
``` 