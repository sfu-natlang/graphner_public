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

cd ../banner-chemdner/
pwd
echo "application.args=train "$labelledConfigFile >nbproject/project.applicationArgs
ant -f build.xml run >& logfile

cd ../Pipeline
pwd
rm MiddleFiles/marginals.txt ; rm MiddleFiles/featureVectors.txt; rm MiddleFiles/sentencelengths.txt; rm MiddleFiles/transitions.txt rm MiddleFiles/marginals_old.txt

echo "labelling labelled set started at"
date

cd ../banner-chemdner/
pwd
echo "application.args=test "$labelledConfigFile >nbproject/project.applicationArgs
ant -f build.xml run 

cd ../Pipeline

echo "Getting reference distributions started at"
date
./Get_Ref_Distributions_For_Tokens.o 0 ../banner-chemdner/output/training.txt MiddleFiles/featureVectors.txt MiddleFiles/Training_Token_Dists.txt 3 3 MiddleFiles/LabelsOrder.txt
./Get_Average_Dists.o 0 MiddleFiles/Training_Token_Dists.txt MiddleFiles/Ref_Dists_Averaged_For_Types 3 3


rm MiddleFiles/marginals.txt ; rm MiddleFiles/featureVectors.txt; rm MiddleFiles/sentencelengths.txt; rm MiddleFiles/transitions.txt rm MiddleFiles/marginals_old.txt

echo "labelling unlabelled set started at"
date

cd ../banner-chemdner/
echo "application.args=test "$unlabelledConfigFile > nbproject/project.applicationArgs
ant -f build.xml run

echo "mention.txt is being modified"
../Pipeline/ModifyBannerMention.o 0 output/mention.txt output/mention_modified.txt

echo "BioCreativeII Perl Evaluation:"
date 

cd ../Data/bc2geneMention/train
pwd
perl alt_eval.perl -gene $GENEFile -altgene $ALTGENEFile  ../../../banner-chemdner/output/mention_modified.txt 

perl alt_eval_detailedVersion.perl -gene $GENEFile -altgene $ALTGENEFile  ../../../banner-chemdner/output/mention_modified.txt > ../../../Pipeline/MiddleFiles/KernelDetailOutput

echo "perl finished"
date

echo "GraphNER main steps started at"
date

cd ../../../Pipeline
pwd
./Get_Average_Dists.o 0 MiddleFiles/marginals.txt MiddleFiles/Average_marginals.txt 3   3  
./banner_chemdner_test_viterbiDecoding.sh $alpha $mu $nu $itrNum $GENEFile $ALTGENEFile 


echo "Experiment finished at"
date

