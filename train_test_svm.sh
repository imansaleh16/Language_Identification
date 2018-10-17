cd lang_id
java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:libs/guava-19.0.jar" lang_id.SVM train
cd liblinear-2.21/
./train ../data/docsMR.txt.svm

cd ..
java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:libs/guava-19.0.jar" lang_id.SVM test

cd liblinear-2.21/
./predict ../data/docsME.txt.svm docsMR.txt.svm.model ../data/svm_out

cd ..
java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:libs/guava-19.0.jar" lang_id.SVM eval
