FROM ubuntu

RUN apt update && apt-get upgrade -y
RUN apt install curl -y
RUN apt install git -y
RUN apt install bash -y
RUN apt install zip -y
RUN apt install maven -y


RUN curl -s "https://get.sdkman.io" | bash
RUN echo 'PATH=$PATH:$HOME/.jenv/bin:$HOME/.sdkman/bin' >> /etc/environment
RUN echo '. $HOME/.sdkman/bin/sdkman-init.sh' >> /etc/environment


RUN bash -c '. /etc/environment && sdk install java 17.0.7-amzn'

RUN apt-get install language-pack-ru -y
RUN echo 'LANG="ru_RU.UTF-8"\nLANGUAGE="ru:en"' >> /etc/default/locale
ENV LANGUAGE ru_RU.UTF-8
ENV LANG ru_RU.UTF-8
ENV LC_ALL ru_RU.UTF-8
RUN locale-gen ru_RU.UTF-8 && dpkg-reconfigure locales
RUN bash -c '. /etc/environment && sdk use java 17.0.7-amzn && sdk install gradle'


WORKDIR /tests
VOLUME /tests
ENV PATH=$PATH:/tests/postgresql-42.6.0.jar
COPY ./test.sh /
ENTRYPOINT bash -c 'bash test.sh'
#ENTRYPOINT bash -c '. /etc/environment && sdk use java 17.0.7-amzn && gradle build && java -classpath /tests/libs/postgresql-42.6.0.jar:/tests/build/libs/consoleApp-1.0-SNAPSHOT.jar org.example.Main'
#CMD ping 127.0.0.1

#sudo docker pull alpine
#sudo docker build -t test .
#docker run -p 32999:32999 --rm -v ./:/tests -it test sh
