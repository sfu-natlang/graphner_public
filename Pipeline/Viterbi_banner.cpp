#include<iostream>
#include<fstream>
#include<vector>
#include<algorithm>
#include<sstream>
#include<iterator>
#include<math.h>
using namespace std;

const double Neg_INF = -100000;
void Tokenize(string Line, vector<string>& tokens)
{
	stringstream ss(Line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}

double mylog(double x)
{
	//cout << "calculating mylog(" << x << ")\n";
	if (x==0)
		return log(0.000001);
	else
		return log(x);
}



string Viterbi(int S, int L,  vector<vector<double>>& emissions, vector<vector<vector<double>>>& transitions,const vector<string>& ngram_lemmas,const  vector<string>& ngrams)
{
	S++; // because of the first </s> and the last <s>
	double V[S][L]; 
	int PTR[L][S];

	// forward pass
	for (int l=0; l<L; l++)
		V[0][l]=mylog(emissions[0][l]);
	for (int pos=1; pos<S; pos++)
		for (int l=0; l<L; l++)
		{
			// V[pos][l]= max_over_labels(emissions[pos][l]*transitions[pos-1][candidatePreviousLabel][l]*V[pos-1][candidatePreviousLabel])
			double current_max = Neg_INF; 
			// cpl stands for candidate Previous Label
			//cout << "transitions to "<< l << "at position " << pos << " :\n";
			for (int cpl=0; cpl< L; cpl++)
				if (current_max< mylog(transitions[pos-1][cpl][l])+V[pos-1][cpl])
				{
					current_max= mylog(transitions[pos-1][cpl][l])+V[pos-1][cpl];
					PTR[l][pos]=cpl;
				}
			V[pos][l]=mylog(emissions[pos][l])+current_max;
		}

	//cout << " before backward pass\n" << endl;
	// backward pass
	// get the label with maximum value in the last row/column
	int argmax = -1;
	double current_max=Neg_INF;
	for (int l=0; l<L; l++)
		if (V[S-1][l]>current_max)
		{
			current_max = V[S-1][l];
			argmax = l;
		}
	string ret = "";
	string tag =ngram_lemmas[S-1];// Names[argmax];
	//tag+="\twinner: "+ Names[argmax] + "\t";
	for (int ctr=0; ctr<argmax; ctr++)
                        tag += "0 ";
                tag+="1 ";
        for (int ctr=argmax+1; ctr<L; ctr++)
                tag+="0 ";
	
	tag+=ngrams[S-1]+"\n";
	//cout << ngrams[S-1] << endl;

	ret = tag;
	int next = argmax;
	for (int pos=S-2; pos>0; pos--)
	{
		tag=ngram_lemmas[pos]+" ";
		int winner_index = PTR[next][pos+1];
		//tag+="\twinner: "+ Names[winner_index] + "\t";
		for (int ctr=0; ctr<winner_index; ctr++)
			tag += "0 ";
		tag+="1 ";
		for (int ctr=winner_index+1; ctr<L; ctr++)
			tag+="0 ";
		tag+=ngrams[pos]+"\n";
		ret = tag + ret;
		next = PTR[next][pos+1];
	}		
	
	return ret;
}

void Process(char* marginalsInputFilePath, char* transitionsInputFilePath, char* sentenceLengthsInputFilePath, char* OutputFilePath, int n, int L)
{
	ifstream inf_marginals; 
	inf_marginals.open(marginalsInputFilePath);
	ifstream inf_transitions;
        inf_transitions.open(transitionsInputFilePath);
	ifstream inf_sentencelengths;
        inf_sentencelengths.open(sentenceLengthsInputFilePath);
	ofstream outf;
	outf.open(OutputFilePath);
	string Line;
	while (getline(inf_sentencelengths,Line).good())
	{		
		vector<string> ngram_lemmas;
		vector<string> ngrams;
		//vector<string> labelNames;
		vector<vector<double>> emissions; // indexed over positions(i.e. positions in the sentence) and labelIndices // keeps the probability of label at position i
		vector<vector<vector<double>>> transitions; // indexed over positions, labelIdices, and labelIndices again. // keeps probability of going from label1 to label2 at position i.

		// the label for <s> can be anything...
		double one_over_L = (double)1/(double)L;
		vector<double> one_emission_vector;
                for (int ctr=n; ctr<L+n; ctr++)
	                one_emission_vector.push_back(one_over_L);
                emissions.push_back(one_emission_vector);

		double one_over_squared_L = (double)1/(double)(L*L);
		vector<vector<double>> uniform_transition_matrix;                
                for (int ctr=0; ctr<L; ctr++)
                {
 	               vector<double> one_transition_row; // all transition probabilities from state ctr;
                       for (int ctr2=0; ctr2<L; ctr2++)
                       		one_transition_row.push_back(one_over_squared_L);                                              
                       uniform_transition_matrix.push_back(one_transition_row);
                }

	
	
		// The first Line, presumably containing the length of the input sentence, is just read;
		vector<string> tokens;
		Tokenize(Line, tokens);
		int S = atoi(tokens[0].c_str());

		// the following two lines are here to take care of the <s> 
		ngram_lemmas.push_back("");
		ngrams.push_back("");

		for (int i=0; i<S; i++)
		{
			tokens.clear();
			getline(inf_marginals,Line);
			
			Tokenize(Line, tokens);
			if (tokens.size()!=2*n+L)
				cout<< "Error in the number of label probabilities in emissions in line " << Line << endl;
		
			string ngram_lemma = "";
			for (int ctr=0; ctr<n; ctr++)
				ngram_lemma+=tokens[ctr]+" ";
			ngram_lemmas.push_back(ngram_lemma); 

			string ngram = "";
                        for (int ctr=n+L; ctr<2*n+L; ctr++)
                                ngram+=tokens[ctr]+" ";
                        ngrams.push_back(ngram);
			
			vector<double> one_emission_vector;			
			for (int ctr=n; ctr<L+n; ctr++)
				one_emission_vector.push_back(atof(tokens[ctr].c_str()));
			emissions.push_back(one_emission_vector);
			
                        getline(inf_transitions,Line);

			if (i==S-1)
				continue;

			tokens.clear();
                        Tokenize(Line, tokens);
                        if (tokens.size()!=(L*L))
                                cout<< "Error in the number of label probabilities in transitions. There should be " << (L*L) << " of them\t" << "but there are " << tokens.size() << "of them in " <<  Line << endl;
                        vector<vector<double>> one_transition_matrix;
                        int ptr=0;
                        for (int ctr=0; ctr<L; ctr++)
                        {
                                vector<double> one_transition_row; // all transition probabilities from state ctr;
                                for (int ctr2=0; ctr2<L; ctr2++)
                                {
                                        one_transition_row.push_back(atof(tokens[ptr].c_str()));
                                        ptr++;
                                }
                                one_transition_matrix.push_back(one_transition_row);
                        }
	                transitions.push_back(one_transition_matrix);

		}

		transitions.push_back(uniform_transition_matrix); // the label for </s> can be anything...

		//read an empty line
		getline(inf_marginals, Line);
		tokens.clear();
		Tokenize(Line, tokens);
		if (tokens.size()>0)
			cout << "This line was supposed to be empty!" << endl;
		
		getline(inf_transitions,Line);
                tokens.clear();
                Tokenize(Line, tokens);
                if (tokens.size()>0)
                        cout << "This line was supposed to be empty!" << endl;


		outf<< Viterbi(S, L, emissions, transitions, ngram_lemmas, ngrams) << endl;		
		
	}

	inf_marginals.close();
	inf_transitions.close();
	inf_sentencelengths.close();
	outf.close();
}

int main(int argc, char** argv)
{
	if (argc!=8)
	{
		cout << "Usage: executable.o 0 marginalsInput transitionsInput SentenceLengthsInput Output_marginals_FilePath n L"<<endl;
		return 1;
	}
	
	Process(argv[2], argv[3], argv[4], argv[5], atoi(argv[6]), atoi(argv[7]));

	return 0;
}
