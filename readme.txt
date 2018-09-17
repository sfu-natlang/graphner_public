contact gsheikhs@sfu.ca with questions.
----------------------------------------------------

This repository contains GraphNER and AML dataset. 

GraphNER is a graph-based semi-supervised tool for named entity recognition that currently uses BANNER and BANNER-ChemDNER as its kernels. For a detailed description of the algorithm in GraphNER and analysis of its performance please refer to the following papers:

  * Sheikhshab, Golnar, Elizabeth Starks, Aly Karsan, Anoop Sarkar, and Inanc Birol. "Graph-based semi-supervised gene mention tagging." In Proceedings of the 15th Workshop on Biomedical Natural Language Processing, pp. 27-35. 2016.
  * Sheikhshab, Golnar, Elizabeth Starks, Aly Karsan, Readman Chiu, Anoop Sarkar, and Inanc Birol. "GraphNER: Using Corpus Level Similarities and Graph Propagation for Named Entity Recognition." In 2018 IEEE International Parallel and Distributed Processing Symposium Workshops (IPDPSW), pp. 229-238. IEEE, 2018.

AML dataset, curated and annotated at BCCancer agency is also described in detail in the following paper:

  ????

In the rest of this readme file, you will see a high-level description of 1) folders in this repository, 2) the latest results on accompanying data and instructions to replicate them, 3) how to test for significance, and 4) instructions on how to use GraphNER with new dataset.
 
----------------------------------------------------

1. Description of folders:
banner: this folder is a copy of BANNER tool[1] that has been integrated into GraphNER as a kernal. BNNER was downloaded from http://banner.sourceforge.net/ and modified to output all the information needed in graph propagation phase. 

banner-chemdner: this is an implementation of BANNER-ChemDNER ([2]) that has been integrated into GraphNER as another kernel. We copied it from https://bitbucket.org/tsendeemts/banner-chemdner and modified it to get all the information needed in graph propagation phase. 

Data: This folder contains the two datasets BC2GM (BioCreativeII gene mention task) and AML (curated internally and described in [4]) as well as graphs we constructed on these datasets. AML was annotated with the same format as BC2GM. Graphs are described as a list of vertices (with ids) and a file that contains all neighbors of each vertex along with the weight of the edge between them. For details of how the graph was constructed please see [3]. 

DetailedOutputs: The latest outputs of BC2GM evaluation perl scripts on different datasets and different methods can be found here. These files could be used for qualitative performance analysis, significance testing, etc. When you run GraphNER on any dataset two DetailedOutput files are generated in Pipeline/MiddleFiles folder which you can copy paste in DetailedOutputs folder for future use. 

Pipleline: This is the folder that contains all of the files that need to be run for a complete experiment. This folder also contains a subfolder named MiddleFiles where temporary files and result files of the experiments are kept. These files get overwritten in every run. 

scripts: This is the folder where you can find shell scripts that you can run for an experiment. The name of each script shows what Kernel and what data set are being used. 

significance_testing: Contains all files needed for significant testing on the output of GraphNER and its kernels. Includes sigf[5] downloaded from https://nlpado.de/~sebastian/software/sigf.shtml.  

------------------------------------------------------

2. Latest results for different datasets and how to obtain them:
(These results are obtained using ant version 1.9 and java version 1.8)

For AML ----iteration0----- of annotations:
cd Data/AML_geneMention__in_bc2geneMentionFormat/train
cp GENE.eval_Iteration0 GENE.eval
cd Data/AML_geneMention__in_bc2geneMentionFormat/test
cp GENE.eval_Iteration0 GENE.eval
cd scripts
./GraphNER_with_BANNER_CHEMDNER_order2__as_Kernel_on_DataSet_AML.sh

Reslut of Kernel (BANNER-ChemDNER):
TP: 2814
FP: 212
FN: 321
Precision: 0.929940515532055 Recall: 0.897607655502392 F: 0.913488070118487

Result of GraphNER:
TP: 2809
FP: 123
FN: 326
Precision: 0.958049113233288 Recall: 0.896012759170654 F: 0.925993077303445

-----------------------

For AML ----iteration1----- of annotations:
cd Data/AML_geneMention__in_bc2geneMentionFormat/train
cp GENE.eval_Iteration1 GENE.eval
cd Data/AML_geneMention__in_bc2geneMentionFormat/test
cp GENE.eval_Iteration1 GENE.eval
cd scripts
./GraphNER_with_BANNER_CHEMDNER_order2__as_Kernel_on_DataSet_AML.sh

Reslut of Kernel (BANNER-ChemDNER):
TP: 2399
FP: 111
FN: 163
Precision: 0.955776892430279 Recall: 0.936377829820453 F: 0.945977917981073

Result of GraphNER:
TP: 2393
FP: 97
FN: 169
Precision: 0.961044176706827 Recall: 0.934035909445746 F: 0.947347585114806

-----------------------

For AML ----iteration2----- of annotations:
cd Data/AML_geneMention__in_bc2geneMentionFormat/train
cp GENE.eval_Iteration2 GENE.eval
cd Data/AML_geneMention__in_bc2geneMentionFormat/test
cp GENE.eval_Iteration2 GENE.eval
cd scripts
./GraphNER_with_BANNER_CHEMDNER_order2__as_Kernel_on_DataSet_AML.sh

Reslut of Kernel (BANNER-ChemDNER):
TP: 2455
FP: 67
FN: 111
Precision: 0.973433782712133 Recall: 0.956742010911925 F: 0.96501572327044

Result of GraphNER:
TP: 2445
FP: 56
FN: 121
Precision: 0.977608956417433 Recall: 0.952844894777864 F: 0.965068087625814

----------------------

For -----BC2GM------:
cd scripts
./GraphNER_with_BANNER_CHEMDNER_order2__as_Kernel_on_DataSet_BC2GM.sh

Reslut of Kernel (BANNER-ChemDNER):
TP: 5308
FP: 803
FN: 1023
Precision: 0.868597610865652 Recall: 0.83841415258253 F: 0.853239029095001

Result of GraphNER:
TP: 5331
FP: 720
FN: 1000
Precision: 0.881011403073872 Recall: 0.842047069973148 F: 0.861088677111937

------------------------------------------------------

3. How to test for Significance:
If you want to know if the results of two models or two annotations are different, you may want to take a test of significance. You can do this by going into significance_test folder and running run_sigf_test.sh. Remember to copy the detailed outputs of the methods or annotations you want to compare from Pipeline/MiddleFiles to DetailedOutputs right after you run the relevant script and to specify what detailed outputs are being compared in run_sigf_test.sh. In case you're comparing methods on BC2GM dataset, you need to change the path of test.in as well. 

------------------------------------------------------

4. How to use GraphNER with new dataset:

1. convert your dataset into the format of Biocreative II gene mention corpus (same format as our corpora in Data folder). You have to put your data in Data folder that has two 
subfolders train and test. train subfolder has to have train.in, GENE.eval, and ALTGENE.eval, and test subfolder has to have test.in, GENE.eval, and ALTGENE.eval similar to what 
BC2GM and AML corpora have. 
2. Construct a graph over your labelled and unlabelled data where the vertices are 3-gram types. Your graph should be represented by two files similar to vertex map and structure
   files in Data/Graphs/ folder. Put these files in the Data/Graphs/ folder. 
3. make train and test config files for both BANNER and BANNER-ChemDNER: 
   change directory to banner/trunk/config, copy and paste train_YOURDATA.xml and test_YOURDATA.xml to new files with your chosen name. Open them and make the relevant changes
   change directory to banner-chemdner/config and repeat the steps. 
4. change directory to scripts/, copy and paste *_YOURDATA.sh to corresponding files with your chosen name, open the files and make the relevant changes and run the scripts. 
5. remember that alpha, mu, nu, and #iterations are hyperparameters that you have to find.

-----------------------------------------------------

References:

[1] Leaman, Robert, and Graciela Gonzalez. "BANNER: an executable survey of advances in biomedical named entity recognition." In Biocomputing 2008, pp. 652-663. 2008.
[2] Munkhdalai, Tsendsuren, Meijing Li, K. Batsuren, and K. H. Ryu. "BANNER-CHEMDNER: incorporating domain knowledge in chemical and drug named entity recognition." In Proceedings of the Fourth BioCreative Challenge Evaluation Workshop, vol. 2, pp. 135-139. 2013.
[3] Sheikhshab, Golnar, Elizabeth Starks, Aly Karsan, Anoop Sarkar, and Inanc Birol. "Graph-based semi-supervised gene mention tagging." In Proceedings of the 15th Workshop on Biomedical Natural Language Processing, pp. 27-35. 2016
[4] AML paper??
[5] Padó, Sebastian. "User’s guide to sigf." Significance testing by approximate randomisation (2006).

-----------------------------------------------------
contact gsheikhs@sfu.ca with questions. 

