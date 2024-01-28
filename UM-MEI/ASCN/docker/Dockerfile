# Dockerfile for the laravel.io application
# TO DO

FROM ubuntu:22.04

# atualizar o sistema
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN apt-get install -y apt-utils

# buscar o repositorio
RUN add-apt-repository ppa:ondrej/php
RUN apt-get update

# definir a timezone
RUN apt-get install -y tzdata
RUN ln -fs /usr/share/zoneinfo/Europe/Lisbon /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata

# instalar PHP 8.2
RUN apt install -y php8.2
RUN apt-get install -y php8.2
RUN apt-get install -y php8.2-zip
RUN apt-get install -y php8.2-mbstring
RUN apt-get install -y php8.2-tokenizer
RUN apt-get install -y php8.2-mysql
RUN apt-get install -y php8.2-gd
RUN apt-get install -y php8.2-xml
RUN apt-get install -y php8.2-bcmath
RUN apt-get install -y php8.2-intl
RUN apt-get install -y php8.2-curl

# instalar nodejs, Composer e NPM
RUN apt install -y nodejs
RUN apt install -y npm

# instalar o git
RUN apt-get update && apt-get install -y git

# clonar o repositorio do LaravelIO
RUN git clone https://github.com/laravelio/laravel.io.git

# instalar curl
RUN apt-get install -y curl

# Get composer | composer setup version 2.2
RUN curl -sS https://getcomposer.org/installer -o /root/composer-setup.php
RUN php /root/composer-setup.php --2.2 --install-dir=/usr/local/bin --filename=composer

# mudar a diretoria para dentro do repositorio
WORKDIR laravel.io

# fazer install do composer e npm 
RUN composer install
RUN npm install -g n && n latest
RUN npm install && npm run build

# instalar o mysql
RUN apt-get update && apt-get install -y mysql-client

# criar ficheiro de configuração
RUN cp .env.example .env

# variavel para migrar
ENV MIGRATE=true

# gerar chave 
RUN php artisan key:generate

# copiar script
COPY ./script.sh .

# permitir a execução do script
RUN chmod +x script.sh

# expor porta 80
EXPOSE 80

CMD ./script.sh