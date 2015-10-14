##
# Script to combine the tables containing alt loci infos from NCBI into a single one
##

## PATHES
regionPath<- "/home/mjaeger/data/NCBI/HomoSapiens/GRCh38.p2/genomic_regions_definitions.txt"
placementPath<- "/home/mjaeger/data/NCBI/HomoSapiens/GRCh38.p2/all_alt_scaffold_placement.txt"
accessionsPath<- "/home/mjaeger/data/NCBI/HomoSapiens/GRCh38.p2/alts_accessions_GRCh38.p2"

## DATA
regions<- read.table(regionPath,header = T,sep="\t",comment.char = "!")
colnames(regions)<- c("region_name","chromosome","region_start","region_stop")

placement<- read.table(placementPath,header = T,sep="\t",comment.char = "!")
placement<- placement[,c(1,3:4,6:ncol(placement))]
colnames(placement)[1]<- "alt_asm_name"

accessions<- read.table(accessionsPath,header = T,sep="\t",comment.char = "!")
accessions<- accessions[,2:ncol(accessions)]

# combine
dat<-  merge(regions,placement,by="region_name")
dat<- merge(dat,accessions,by.x="alt_scaf_acc",by.y="RefSeq.Accession.version")
dat<- dat[order(dat$region_name,dat$alt_asm_name),]


write.table(dat,"../data/combinedAltLociInfo.tsv",row.names=F,sep="\t",quote=F)

