cd mallet
mvn install
mvn package

cd ../lang_id/
mvn package
[ ! -d "data" ] && mkdir "data"
[ ! -d "model_data" ] && mkdir "model_data"
