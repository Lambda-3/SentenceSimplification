[![Build Status](https://travis-ci.org/Lambda-3/SentenceSimplification.svg?branch=master)](https://travis-ci.org/Lambda-3/SentenceSimplification)

# Sentence Simplification
compile and build with:
    
	mvn clean compile assembly:single

run with:
    
	java -jar target/sentence-simplification-jar-with-dependencies.jar <input_file> <output_file>

or you can use it as a library.

Some examples of its usage as a library are the [DiscourseSimplification](https://github.com/Lambda-3/DiscourseSimplification) and inside the [Graphene](https://github.com/Lambda-3/Graphene) pipeline.
