#include<iostream>
#include<fstream>
#include<vector>
#include<iterator>
#include<algorithm>
#include<sstream>

using namespace std;

void Tokenize(string line, vector<string>& tokens, string delimiter=" \t")
{
	for (int i=0; i<delimiter.size(); i++)
		replace(line.begin(), line.end(), delimiter[i], ' ');

	stringstream ss(line);	
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}

bool valid_parantheses(string str)
{
	int open1 = 0;
	int open2 = 0;
	int open3 = 0;

	for (int i=0; i<str.size(); i++)
		if (str[i]=='(')
			open1++;
		else if (str[i]==')')
		{
			open1--;
			if (open1<0)
				return false;
		}
		else if (str[i]=='{')
                        open2++;
                else if (str[i]=='}')
                {
                        open2--;
                        if (open2<0)
                                return false;
                }
		else if (str[i]=='[')
                        open3++;
                else if (str[i]==']')
                {
                        open3--;
                        if (open3<0)
                                return false;
                }



	if ((open1!=0)||(open2!=0)||(open3!=0))
		return false;

	
	return true;

}

int main(int argc, char** argv)
{
	if (argc!= 7)
	{
		cout << "executable.o 0 ids_FilePath marginals_FilePath output_FilePath n LabelOrderFile" << endl;
		return 1;
	}

	ifstream inf_ids;
	inf_ids.open(argv[2]);
	ifstream inf_marginals;
	inf_marginals.open(argv[3]);
	ofstream outf;
	outf.open(argv[4]);
	int n = atoi(argv[5]);	
	if (n%2!=1)
	{
		cout << "n should be odd!" << endl;
		return 1;
	}
	int middle = (n-1)/2;

	int current_offset=0;
	int start=-1;
	int end=-1;
	string current_literal="";
	string label;

	string line;
	string current_id;
	getline(inf_ids, current_id);
	while (getline(inf_marginals,line).good())
	{
		vector<string> tokens;
		Tokenize(line, tokens, " \t");
		if (tokens.size()==0)
		{
			//cout << endl;
			if (current_literal.size()>0)
                        {
                                if ((start != -1) && (valid_parantheses(current_literal))) // if we have I after O, start will be -1. We are ignoring such sequences (IIII instead of BIII)
                                	outf<< current_id<<"|"<<start << " " << end << "|" << current_literal << endl;
                                current_literal = "";
                                start=-1;
                                end = -1;
                        }

			getline(inf_ids, current_id);
			//cout << current_id << "\t" << endl;
			current_offset=0;
			continue;
		}
		if (tokens.size()!=2*n+3) // assumption:  L=3 because there are three labels: O, B, I whose probabilities are in marginals file with the mentioned order
		{	
			cout << "incorrect number of tokens in " << line << endl;
			return 1;
		}
		string word = tokens[n+3+middle];

		//Golnar started on July 15th, 2016

		ifstream inf_order;
	        inf_order.open(argv[6]);
        	string orderLine;
	        vector<string> order_tokens;
        	getline(inf_order, orderLine);
	        Tokenize(orderLine, order_tokens, " ");
        	inf_order.close();		


		int O_index = -1;
		int I_index = -1;
		int B_index = -1;

		for (int i=0; i<3; i++)
			if (order_tokens[i]=="O")
				O_index = i;
			else if (order_tokens[i]=="B-GENE")
				B_index =i; 
			else if (order_tokens[i]=="I-GENE")
                                I_index =i;

		if ((O_index == -1) || (B_index == -1) || (I_index == -1) )
		{
			cout << "ERROR!";
			char x;
			cin >> x;
		}
		//Gonar finished on July 15th, 2016


		float p_O = atof(tokens[n+O_index].c_str()); // Golnar added +O_index on July 15th, 2016
		float p_B = atof(tokens[n+B_index].c_str()); // Golnar replaced +1 by +B_index on July 15th, 2016
		float p_I = atof(tokens[n+I_index].c_str()); // Golnar replaced +2 by +I_index on July 15th, 2016

		if ((p_O > p_B) && (p_O > p_I))
			label = "O";		
		else if ((p_B>=p_O) && (p_B >= p_I))
			label = "B";
		else 
			label = "I";

		//cout << label<<endl; 

		if ((label=="O")||(label=="B"))
		{
			if (current_literal.size()>0)
			{
				if (start == -1)
					start = 0; // added later-on (on Sep 1st) to address a bug. Why it might be -1 should be investigated further.
				if (valid_parantheses(current_literal)) // Added by Golnar -- March 14, 2016
					outf<< current_id<<"|"<<start << " " << end << "|" << current_literal << endl;
				current_literal = "";
				start=-1;
				end = -1;
			}
		}
		if (label=="B")
		{
			//cout << word << "/B ";
			current_literal = word;
			start = current_offset;
			end = current_offset+word.size()-1;	
		}
		else if (label=="I")
		{
			//cout << word << "/I ";
			current_literal += " "+word;
			end = current_offset+word.size()-1;
		}
		/*else 
			cout << word << "/O ";*/
		

		current_offset+=word.size();
	}
	
	inf_ids.close();
	inf_marginals.close();
	outf.close();
	return 0;
}

