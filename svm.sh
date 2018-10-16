cd lang_id
java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:libs/guava-19.0.jar" lang_id.SVM "../$1"
cd liblinear-2.21/
./predict "../../$1.svm" docsMR.txt.svm.model ../../svm_answer
cd ..
java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:libs/guava-19.0.jar" lang_id.SVM lang
