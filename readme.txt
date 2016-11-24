How to replicate the numbers in GraphNER paper:

1. make sure you're using java version 1.7
2. make sure you have ant 1.9 or higher
3. for replicating results on BC2GM, just change directory to sripts/  and run the relevant .sh files
4. for AML corpus, you need to have access to AML raw texts (test.in and train.in files). These files will be public as soon as the paper work with publishers are finished. 
   you need to copy the raw abstracts in your train.in file into Data/AML_geneMention__in_bc2geneMentionFormat/train/train.in file and the copy of raw abstracts in your
   test.in file into into Data/AML_geneMention__in_bc2geneMentionFormat/test/test.in and then change directory to sripts/  and run the relevant .sh files


How to use GraphNER with new dataset:

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

contact gsheikhs@sfu.ca with questions. 






 
