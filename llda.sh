cd lang_id

java -cp "target/lang_id-0.0.1-SNAPSHOT.jar:../mallet/target/mallet-0.0.1-SNAPSHOT.jar:../mallet/libs/mallet-2.0.8.jar:../mallet/libs/bsh-2.0b4.jar:../mallet/libs/trove4j-2.0.2.jar"  lang_id.LLDA "../$1"

