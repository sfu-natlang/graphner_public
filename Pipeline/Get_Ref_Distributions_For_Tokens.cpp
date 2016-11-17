#include <iostream>
#include <fstream>
#include <unordered_map>
#include <vector>
#include <algorithm>
#include <iterator>
#include <sstream>

using namespace std;

void Tokenize(string line, vector<string> & tokens, string delimiter=" \t")
{
	for (int i=0; i<delimiter.size(); i++)
		replace(line.begin(), line.end(), delimiter[i], ' ');

	stringstream ss(line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}


int main(int argc, char** argv)
{
	if (argc!=8 )
	{
		cout << "Usage: executable.o 0 TrainingFile FeatureVectors outputFile n L(number of labesls) LabelOrderFile)\n ";
		return 1;
	}

	unordered_map<string, int> labels;

	int n= atoi(argv[5]);
	int L= atoi(argv[6]);

	ifstream inf_order;
	inf_order.open(argv[7]);
	string orderLine;
	vector<string> tokens;
	getline(inf_order, orderLine);
	Tokenize(orderLine, tokens, " ");
	inf_order.close();
	int ctr = 0;
	for (int i=0; i<L; i++)
	{
		labels[tokens[i]]=ctr++;
		//cout << "adding label " << "\"" << argv[7+i] << "\"" << endl;
	}
	
	ifstream inf_train;
	ifstream inf_ngrams;
	inf_train.open(argv[2]);
	inf_ngrams.open(argv[3]);
	ofstream outf;
	outf.open(argv[4]);

	string train_line, ngrams_line;
	while (getline(inf_train, train_line).good())
	{
		//cout << train_line << endl;
		vector<string> train_tokens;
		Tokenize(train_line, train_tokens, " \t");
		//cout << "number of words: " << train_tokens.size() << endl;
		for (int i=0; i< train_tokens.size(); i++)
		{
			getline(inf_ngrams, ngrams_line);
			vector<string> ngram_tokens;
			Tokenize(ngrams_line, ngram_tokens);
			//cout << "1\n";
			//cout << ngrams_line << endl;
			for (int ctr=0; ctr<n; ctr++)
				outf<<ngram_tokens[ctr]<<" ";
			outf<<"\t";
			string strLabel = train_tokens[i].substr(train_tokens[i].find_last_of('|')+1);
			//cout << "\"" << strLabel << "\"" << endl;
			//cout <<"2\n";
			int label = labels[strLabel];
			if (labels.size()>L)
			{
				cout <<"error! unexpected label was seen in " << train_tokens[i] << endl ;	
				return 1;
			}
			for (int l=0; l<L; l++)
				if (l!=label)
					outf<<"0\t";
				else
					outf<<"1\t";
			outf<<endl;
			//cout << "3\n";
		}
		
		getline(inf_ngrams, ngrams_line);// this file is supposed to have an empty line after each sentence. 
	}

	inf_train.close();
	inf_ngrams.close();
	outf.close();
}
