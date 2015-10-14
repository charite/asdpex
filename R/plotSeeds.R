##
# R script visualizing the used seeeds and bands for the bandedchainalignment
##

setwd("~/git/hg38altLociSelector")

seeds<- dir("seed/",pattern = "extended.tab$")
sample<- read.table(paste("seed",seeds[1],sep = "/"),header=F,sep="\t")

plot.window(range(sample$V1) , range(sample$V2) )
plot.new()

plot(sample[,1:2],type="n",xlab="reference",ylab="alt loci")
#axis(site=1)
mycols<- rainbow(nrow(sample))
for(i in 1:nrow(sample)){
  lines(c(sample[i,1],sample[i,1]+sample[i,3]),c(sample[i,2],sample[i,2]+sample[i,3]),col=mycols[i])
}



sample<- read.table(paste("seed",seeds[1],sep = "/"),header=F,sep="\t")

                    