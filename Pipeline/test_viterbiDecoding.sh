#!/bin/sh

alpha=$1
mu=$2
nu=$3
itr=$4

GENEFile=$5 
ALTGENEFile=$6 

./Graph_Propagate.o 0 MiddleFiles/BCGM_GraphStructure.txt MiddleFiles/BCGM_VertexMap.txt MiddleFiles/Average_marginals.txt MiddleFiles/Ref_Dists_Averaged_For_Types $itr $mu $nu MiddleFiles/marginals_after_graph_propagation.txt 3 1000000 >& logfile
./Combine_CRF_and_Graph_Dists.o 0 MiddleFiles/marginals.txt MiddleFiles/marginals_after_graph_propagation.txt MiddleFiles/marginals_combined $alpha 3 3 >& logfile
./Viterbi_banner.o 0 MiddleFiles/marginals_combined MiddleFiles/transitions.txt MiddleFiles/sentencelengths.txt MiddleFiles/marginals_combined_viterbi.txt 3 3 >& logfile
./Get_GENE_eval_from_marginals_and_ids.o 0 ../banner/trunk/output/ids.txt MiddleFiles/marginals_combined_viterbi.txt MiddleFiles/GENE.eval_marginals_combined_viterbi 3 MiddleFiles/LabelsOrder.txt >& logfile


cd ../Data/bc2geneMention/train

echo "GraphNER performance:"
perl alt_eval.perl -gene $GENEFile -altgene $ALTGENEFile ../../../Pipeline/MiddleFiles/GENE.eval_marginals_combined_viterbi

perl alt_eval_detailedVersion.perl -gene $GENEFile -altgene $ALTGENEFile ../../../Pipeline/MiddleFiles/GENE.eval_marginals_combined_viterbi > ../../../Pipeline/MiddleFiles/GraphNerDetailedOutput
