CXX=g++   

## This makefile downloads the latest version of the SeqAn library, which is needed to 
## compile regionalign2vcf, and then compiles the executable regionalign2vcf
## It should be possible to update the version of SeqAn as new version become available
## by altering the following paths as required.

SEQAN=seqan-library-2.0.1 
SEQANURI=http://packages.seqan.de/seqan-library/seqan-library-2.0.1.tar.bz2


CXXFLAGS+= -W -Wall -Wno-long-long -pedantic -Wno-variadic-macros 
CXXFLAGS+=-I ./seqan-library-2.0.1/include



default: all
all: regionalign2vcf



$(SEQAN):
	echo "Downloading $(SEQANURI)..."
	wget $(SEQANURI)
	tar -jxvf seqan-library-2.0.1.tar.bz2





regionalign2vcf: regionalign2vcf.cpp $(SEQAN)
	$(CXX) $< -o $@ $(CXXFLAGS)	


clean:
	rm -f regionalign2vcf

.PHONY: all clean


