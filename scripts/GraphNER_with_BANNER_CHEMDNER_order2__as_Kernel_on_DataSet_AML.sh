cp ../Data/Graphs/AML_AllFeatures_LesThan10000_VertexMap.txt ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/AML_AllFeatures_LesThan10000_GraphStructure.txt ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./banner_chemdner_Experiment_on_new_DataSet.sh  2  1 1 2  config/banner_order2_AML_brown1000pubmed_simple_tokenized_WVC500_WST_prod.xml config/banner_order2_AML_brown1000pubmed_simple_tokenized_WVC500_WST_test_prod.xml ../../AML_geneMention__in_bc2geneMentionFormat/test/GENE.eval ../../AML_geneMention__in_bc2geneMentionFormat/test/ALTGENE.eval
