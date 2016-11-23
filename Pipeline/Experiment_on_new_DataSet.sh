#!/bin/sh

alpha=$1
mu=$2
nu=$3
itrNum=$4

labelledConfigFile=$5 
unlabelledConfigFile=$6 

GENEFile=$7 
ALTGENEFile=$8


echo "training CRF started at"
date

cd ../banner/trunk
./scripts/banner.sh train $labelledConfigFile >& logfile

cd ../../Pipeline
rm MiddleFiles/marginals.txt ; rm MiddleFiles/featureVectors.txt; rm MiddleFiles/sentencelengths.txt; rm MiddleFiles/transitions.txt; rm MiddleFiles/marginals_old.txt

echo "labelling labelled set started at"
date
pwd
cd ../banner/trunk
pwd
./scripts/banner.sh test $labelledConfigFile >& logfile

cd ../../Pipeline

echo "Getting reference distributions started at"
date

./Get_Ref_Distributions_For_Tokens.o 0 ../banner/trunk/output/training.txt MiddleFiles/featureVectors.txt MiddleFiles/Training_Token_Dists.txt 3 3 MiddleFiles/LabelsOrder.txt
./Get_Average_Dists.o 0 MiddleFiles/Training_Token_Dists.txt MiddleFiles/Ref_Dists_Averaged_For_Types 3 3


echo "labelling unlabelled set started at"
date

rm MiddleFiles/marginals.txt ; rm MiddleFiles/featureVectors.txt; rm MiddleFiles/sentencelengths.txt; rm MiddleFiles/transitions.txt; rm MiddleFiles/marginals_old.txt
pwd
cd ../banner/trunk
./scripts/banner.sh test $unlabelledConfigFile >& logfile

echo "mention.txt is being modified"
../../Pipeline/ModifyBannerMention.o 0 output/mention.txt output/mention_modified.txt

echo "BioCreativeII Perl Evaluation:"
date 

cd ../../Data/bc2geneMention/train

echo "Kernel Performance:"

perl alt_eval.perl -gene $GENEFile -altgene $ALTGENEFile ../../../banner/trunk/output/mention_modified.txt 

perl alt_eval_detailedVersion.perl -gene $GENEFile -altgene $ALTGENEFile ../../../banner/trunk/output/mention_modified.txt > ../../../Pipeline/MiddleFiles/KernelDetailOutput
echo "perl finished"
date 

cd ../../../Pipeline

echo "GraphNER main steps started at"
date

./Get_Average_Dists.o 0 MiddleFiles/marginals.txt MiddleFiles/Average_marginals.txt 3   3  
./test_viterbiDecoding.sh $alpha $mu $nu $itrNum $GENEFile $ALTGENEFile 

echo "Experiment finished at"
date



