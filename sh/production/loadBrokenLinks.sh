#!/bin/bash

HOME=/home/javaapps/SocialCheckLinks/social-checklinks

java -cp $HOME/build/web/WEB-INF/classes:$HOME/lib/mongo-java-driver-2.12.4.jar:$HOME/build/web/WEB-INF/lib/Bruma.jar br.bireme.scl.BrokenLinks /usr/local/bireme/java/FisChecker/output/${1}_out/bases/${1}_v8broken.txt $HOME/$1 mongodb.bireme.br -outFileEncoding=IBM850 -outMstEncoding=IBM850 $2
