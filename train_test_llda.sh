#cd lang_id

#paste data/docsMR.files data/docsMR.labels data/docsMR.txt > data/docsMR.llda
#paste data/docsME.files data/docsME.labels data/docsME.txt > data/docsME.llda

# generate ngrams to train llda
#java -cp target/lang_id-0.0.1-SNAPSHOT.jar lang_id.LLDA ngrams

#cd ..
cd mallet

#java -cp "target/mallet-0.0.1-SNAPSHOT.jar:libs/bsh-2.0b4.jar:libs/trove4j-2.0.2.jar" cc.mallet.topics.LabeledLDA train --input "../lang_id/data/docsMR.llda.4grams" --output-prefix labeled. --output-model "../lang_id/model_data/llda.model.ser" --output-topic-keys "../lang_id/model_data/llda_topic_keys" --output-topic-docs "../lang_id/model_data/llda_topic_docs" --num-iterations 2000

java -cp "target/mallet-0.0.1-SNAPSHOT.jar:libs/mallet-2.0.8.jar:libs/bsh-2.0b4.jar:libs/trove4j-2.0.2.jar" cc.mallet.topics.LabeledLDA test

cd ..
cd lang_id

java -cp target/lang_id-0.0.1-SNAPSHOT.jar lang_id.LLDA eval
