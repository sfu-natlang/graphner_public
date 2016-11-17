#include <fstream>
#include <iostream>
#include <vector>
#include <algorithm>
#include <sstream>
#include <iterator>


using namespace std;

void Tokenize(string line, vector<string>& tokens, string delimiter=" \t")
{
	for (int i=0; i<delimiter.size(); i++)
		replace(line.begin(), line.end(), delimiter[i], ' ');
	stringstream ss(line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}

int main(int argc, char** argv)
{
	if (argc!= 4)
	{
		cout << "Usage: executable.o 0 inputMentionFilePath outputMentionFilePath";
		return 1;
	}

	ifstream inf;
	inf.open(argv[2]);
	ofstream outf;
	outf.open(argv[3]);

	string line;
	while (getline(inf, line).good())
	{
		vector<string> tokens;
		Tokenize(line, tokens," |");
		
		if (tokens.size()<4)
			cout << line << " is a strange line\n";
		else
		{
			outf<< tokens[0] << "|" << tokens[1] << " ";
			int end=atoi(tokens[1].c_str())-1;
			for (int i=3; i<tokens.size(); i++)
				end+=tokens[i].length();
			outf<< end << "|";
			for (int i=3; i<tokens.size(); i++)
				outf<<tokens[i]<< " ";
		 	outf<< endl; 
		}		
	}

	inf.close();
	outf.close();
}
