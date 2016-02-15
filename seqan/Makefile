CXX=g++   ### alternatively: ##
#CXX=clang

CXXFLAGS+= -W -Wall -Wno-long-long -pedantic -Wno-variadic-macros 
CXXFLAGS+=-I ./seqan-library-2.0.1/include

SEQANURI=http://packages.seqan.de/seqan-library/seqan-library-2.0.1.tar.bz2






SEQAN:
	wget ${SEQANURI}
	tar -jxvf $(basename ${SEQANURI})



regionalign2bed: regionalign2vcf.cpp
	$(CXX) regionalign2vcf.cpp -o regionalign2vcf $(CXXFLAGS)	



default: all
all: regionalign2vcf


clean:
	rm -f regionalign2vcf

.PHONY: default all clean 
