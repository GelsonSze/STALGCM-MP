import java.io.*;
import java.util.*;

    class DFA {
        
        private String name;
        private State initialstate;
        private ArrayList<State> states; //list of states
        private ArrayList<Edge> edges; //list of edges
        private ArrayList<String> alphabet; //list of symbols
        private ArrayList<State> finalstates; //list of final states
        private ArrayList<Partition> partition = new ArrayList<Partition>(); // partition arraylist
        private ArrayList<Partition> partition2 = new ArrayList<Partition>();

        public DFA(NFA nfa){
            this.name = nfa.getName();
            initialstate = nfa.getinitialState();
            states = new ArrayList<>();
            states.add(initialstate);

            edges = new ArrayList<>();
            alphabet = nfa.getAlphabet();
            finalstates = new ArrayList<>();

            NFAtoDFA(nfa);
        }

        private void NFAtoDFA(NFA nfa){

            //initial state index 0
            ArrayList<Edge> tempedges = nfa.getEdges();
            ArrayList<String> tempalphabet = nfa.getAlphabet();
            boolean end = false;
            State from,to;
            String symbol, statename;
            String[] statelist,tempstatelist;
            Edge tempedge;
            int index = 0;

            while(!end){

                from = this.states.get(index);
                statelist = from.getName().split("-");
            
                //gets next state for every symbol in every state
                for(int i=0;i<tempalphabet.size();i++){
                    
                    statename = "";
                    symbol = tempalphabet.get(i);

                    for(int j=0;j<statelist.length;j++){
                        from = nfa.getState(statelist[j]);
                        
                        if(from==null)
                            from = new State("{}");

                        for(int k=0;k<tempedges.size();k++){

                            tempedge = tempedges.get(k);

                            if(from.equals(tempedge.getFrom()) 
                            && symbol.equals(tempedge.getSymbol()))
                                statename += tempedge.getTo().getName() + "-";
                        }
                    }

                    //if no transitions, empty set ({})
                    if(statename.length()==0)
                        statename = "{}";
                    else // gets rid of excess dash (-)
                        statename = statename.substring(0,statename.length()-1);

                    //sorts statenames into alphabetical order
                    tempstatelist = statename.split("-");
                    Arrays.sort(tempstatelist);

                    //guarantees no duplicates
                    statename = "";
                    for(int j=0;j<tempstatelist.length;j++){

                        if(!statename.contains(tempstatelist[j]))
                            statename += tempstatelist[j] + "-";
                    }
                    //gets rid of excess dash (-)
                    statename = statename.substring(0,statename.length()-1);

                    //adding state transitions to edges
                    to = new State(statename);

                    if(!this.states.contains(to))
                        this.states.add(to);
                    else
                        to = this.states.get(this.states.indexOf(to));
                    
                    from = this.states.get(index);

                    this.edges.add(new Edge(from, symbol, to));
                }
                
                index++;

                //while theres still states with unfilled transitions
                if(this.states.size()==index)
                    end = true;
            }

            //add final states
            for(int i=0;i<nfa.getFinalStates().size();i++){
                for(int j=0;j<this.states.size();j++){
                    if(this.states.get(j).getName().contains(nfa.getFinalStates().get(i).getName())
                    && !this.finalstates.contains(this.states.get(j))){
                        this.states.get(j).setFinal();
                        this.finalstates.add(this.states.get(j));
                    }
                }
            }
        }

        public boolean DFAequivalence(DFA dfa, DFA dfa2){
            partition.clear();
            partition2.clear();
            partition.add(new Partition());
            partition.add(new Partition()); // first two partitions

            for (int i = 0; i < dfa.getStates().size(); i++) { //partitioned by checking final states
                if(dfa.getStates().get(i).getFinal()){
                    partition.get(0).getStates().add(new String(dfa.getStates().get(i).getName())+("/M1"));
                }
                else{
                    partition.get(1).getStates().add(new String(dfa.getStates().get(i).getName())+("/M1"));
                }
            }

            for (int i=0; i < dfa2.getStates().size(); i++) { //partitioned by checking final states
                if(dfa2.getStates().get(i).getFinal()){
                    partition.get(0).getStates().add(new String(dfa2.getStates().get(i).getName())+("/M2"));
                }
                else{
                    partition.get(1).getStates().add(new String(dfa2.getStates().get(i).getName())+("/M2"));
                }
            }

            boolean reduced = false;
            String [] grouping = new String[dfa.getAlphabet().size()]; 
            String temp = "";
            int x=0;
            Set<String> distinctSet;
            String[] tempstate = new String[2];
            String[] tempstate2 = new String[2];
            
            while(!reduced){
                for (int j = 0; j < partition.size(); j++){ //loop all partitions
                    for (int i = 0; i < partition.get(j).getStates().size(); i++) { //loop all states
                        x=0; 

                        //idea: if else to check if current state is in dfa1 or 2 using the substring
                        //then loop the edges of the matching dfa
                        //find out how to split string
                        tempstate = partition.get(j).getStates().get(i).split("/");
                        if(tempstate[1].equals("M1")){
                            for (int k = 0; k < dfa.getEdges().size(); k++){ //loop all transitions
                                if(dfa.getEdges().get(k).getFrom().getName().equals(tempstate[0])){ //same state(from) ^this reads the edges and executes when the state in the partition matches the from edge state
                                    for (int l = 0; l < alphabet.size(); l++){
                                        if(alphabet.get(l).equals(dfa.getEdges().get(k).getSymbol())){
                                            grouping[l] = dfa.getEdges().get(k).getTo().getName()+("/M1"); 
                                            x++; break;
                                        }
                                    }
                                }
                                if(x==alphabet.size()){
                                    break;
                                }
                            }
                        }
                        else if(tempstate[1].equals("M2")){
                            for (int k = 0; k < dfa2.getEdges().size(); k++){ //loop all transitions
                                if(dfa2.getEdges().get(k).getFrom().getName().equals(tempstate[0])){ //same state(from) ^this reads the edges and executes when the state in the partition matches the from edge state
                                    for (int l = 0; l < alphabet.size(); l++){
                                        if(alphabet.get(l).equals(dfa2.getEdges().get(k).getSymbol())){
                                            grouping[l] = dfa2.getEdges().get(k).getTo().getName()+("/M2"); 
                                            x++; break;
                                        }
                                    }
                                }
                                if(x==alphabet.size()){
                                    break;
                                }
                            }
                        }
                        else{
                            System.out.println("oi its not m1 or m2");
                        }
                        
                        temp = "";
                        for (int l = 0; l < alphabet.size(); l++){                       
                            for (int k = 0; k < partition.size(); k++){
                                if(partition.get(k).getStates().contains(grouping[l])){
                                    temp += k; break;
                                }
                            }    
                        }
                        partition.get(j).getGroup().add(temp); //add groupings to arraylist till finished
                        //add grouping to the start of each state but not here since it will mess .contains
                    }
                }

                if(checkReduced(partition)){ //if no distinct groups break loop
                    break;
                }

                partition2 = new ArrayList<>(partition); //shallow copy
                partition.clear();

                temp = "";
                x=-1;
                for (int j = 0; j < partition2.size(); j++){ //loops through all old partitions
                    distinctSet = new HashSet<>(partition2.get(j).getGroup()); 
                    for(int k=0; k<distinctSet.size(); k++){
                        partition.add(new Partition()); //new partitions depending on how many unique groups
                    }

                    x++;
                    //add the group to the start of the state name then sort the states instead
                    String temp2 = new String();
                    for(int i=0; i<partition2.get(j).getStates().size(); i++){
                        temp2 = partition2.get(j).getGroup().get(i) + "#" + partition2.get(j).getStates().get(i);
                        partition2.get(j).getStates().set(i, temp2);
                    }
                    Collections.sort(partition2.get(j).getStates()); //sorts the concatenation
                    
                    tempstate2 = partition2.get(j).getStates().get(0).split("#");

                    temp = tempstate2[0];
                    for(int i=0; i<partition2.get(j).getStates().size(); i++){ //loops through all states
                        
                        tempstate2 = partition2.get(j).getStates().get(i).split("#");

                        if(!temp.equals(tempstate2[0])){ //if not
                            x++; //move on to the next new partition
                            temp = tempstate2[0];
                        }

                        partition.get(x).getStates().add(tempstate2[1]); //add states to new partitions
                    }
                }
            }
            //if statement to check if the initial states are in the same partition
            for (int i = 0; i < partition.size(); i++){
                String initial1 = dfa.getInitialState().getName()+"/M1";
                String initial2 = dfa2.getInitialState().getName()+"/M2";
                if(partition.get(i).getStates().contains(initial1) && partition.get(i).getStates().contains(initial2)){
                    return true;
                }            
            }
            return false;
        }

        private boolean checkReduced(ArrayList<Partition> x){
            Set<String> distinctSet;
            for (int j = 0; j < x.size(); j++){
                distinctSet = new HashSet<>(x.get(j).getGroup());
                if(distinctSet.size()>1){
                    return false;
                }
            }
            return true;
        }

        public String getName(){
            return name;
        }

        public State getInitialState(){
            return initialstate;
        }

        public ArrayList<State> getStates(){
            return states;
        }

        public ArrayList<Edge> getEdges(){
            return edges;
        };

        public ArrayList<String> getAlphabet(){
            return alphabet;
        };

        public ArrayList<State> getFinalStates(){
            return finalstates;
        };

        public void printDFA(){
            System.out.println(states);
            System.out.println(alphabet);
            System.out.println(edges);
            System.out.println(initialstate);
            System.out.println(finalstates);
        }

        public void printFinalPartition(){
            for(int i=0; i<partition.size(); i++){
                System.out.println("S"+i);
                partition.get(0).printPartition();
            }
        }

    }
    class Edge {

        private String symbol;
        private State from;
        private State to; 

        public Edge(State state1, String symbol, State state2){
            this.from = state1;
            this.symbol = symbol;
            this.to = state2;
        }

        public State getFrom(){
            return from;
        }
        
        public State getTo(){
            return to;
        }
        
        public String getSymbol(){
            return symbol;
        }

        @Override
        public String toString(){
            return "From: " + this.from.toString() + " Symbol: " + symbol + " To: " + this.to.toString();
        }
    }
    class NFA {
        
        private String name;
        private State initialstate;
        private ArrayList<State> states; //list of states
        private ArrayList<Edge> edges; //list of edges
        private ArrayList<String> alphabet; //list of symbols
        private ArrayList<State> finalstates; //list of final states

        public NFA(String name){
            this.name = name;
            initialstate = null;
            states = new ArrayList<>();
            edges = new ArrayList<>();
            alphabet = new ArrayList<>();
            finalstates = new ArrayList<>();
        }

        public void addState(State temp){
            states.add(temp);
        }

        public void addEdge(State state1, String symbol, State state2){
            edges.add(new Edge(state1, symbol, state2));
        }

        public void addAlphabet(String temp){
            alphabet.add(temp);
        }

        public void addInitial(State temp){
            temp.setIntial();
            this.initialstate = temp;
        }

        public void addFinalState(State temp){
            temp.setFinal();
            finalstates.add(temp);
        }
        
        public State getState(String temp){
            int index = states.indexOf(new State(temp));
            if(index==-1)
                return null;
            return states.get(index);
        }

        public State getinitialState(){
            return initialstate;
        }

        public String getName(){
            return name;
        }

        public ArrayList<State> getStates(){
            return states;
        }

        public ArrayList<Edge> getEdges(){
            return edges;
        };

        public ArrayList<String> getAlphabet(){
            return alphabet;
        };

        public ArrayList<State> getFinalStates(){
            return finalstates;
        };

        public void printNFA(){
            System.out.println(states);
            System.out.println(alphabet);
            System.out.println(edges);
            System.out.println(initialstate);
            System.out.println(finalstates);
        }
    }
    class Partition {
        
        private ArrayList<String> states; //list of states
        private ArrayList<String> group; //list of groupings

        public Partition(){
            states = new ArrayList<>();
            group = new ArrayList<>();
        }
        
        public ArrayList<String> getStates(){
            return states;
        }

        public ArrayList<String> getGroup(){
            return group;
        }

        public void printPartition(){
            System.out.println(states);
            for(int i=0; i<group.size(); i++){
                System.out.println(group.get(i));
            }
        }
    }
    class State {

        private String name;
        private boolean initialstate;
        private boolean finalstate;

        public State(String name){
            this.name = name;
            this.finalstate = false;
            this.initialstate = false;
        }
        
        public String getName(){
            return name;
        }

        public boolean getInitial(){
            return initialstate;
        }

        public boolean getFinal(){
            return finalstate;
        }

        public void setFinal(){
            this.finalstate = true;
        }

        public void setIntial(){
            this.initialstate = true;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof State)) 
                return false;
            String temp = ((State)o).getName();
            return this.getName().equals(temp);
        }

        @Override
        public String toString(){
            String temp = this.name;
            return temp;
        }
    }

public class MP {
    public static void main(String args[]) throws IOException{

        BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
        String machinename, tempstimulus;
        ArrayList<NFA> nfaList = new ArrayList<>();
        NFA tempNFA = null;
        DFA tempDFA = null;
        int numstate, numstimulus, numtransitions, numfinal, nummachines;
        State tempfrom, tempto, tempfinal;
        ArrayList<DFA> dfaList = new ArrayList<>();

        int counter = 0;

        nummachines = Integer.parseInt(kb.readLine());
        kb.readLine();

        ArrayList<String> EquivalentList[] = new ArrayList[nummachines];

            while(counter != nummachines){

                machinename = kb.readLine();
                tempNFA = new NFA(machinename);

                //states
                numstate = Integer.parseInt(kb.readLine());
                
                
                for(int i = 0; i<numstate; i++){
                    tempNFA.addState(new State(kb.readLine()));
                }

                //symbols
                numstimulus = Integer.parseInt(kb.readLine());
                for(int i = 0; i<numstimulus; i++){
                    tempNFA.addAlphabet(new String(kb.readLine()));
                }

                //transitions
                numtransitions = Integer.parseInt(kb.readLine());
                for(int i = 0; i<numtransitions; i++){
                    String[] transitionstring = kb.readLine().split(" ");
                    tempfrom = tempNFA.getState(transitionstring[0]);
                    tempstimulus = transitionstring[1];
                    tempto = tempNFA.getState(transitionstring[2]);
                    tempNFA.addEdge(tempfrom, tempstimulus, tempto);
                }

                //intial state
                tempNFA.addInitial(tempNFA.getState(kb.readLine()));

                //final states
                numfinal = Integer.parseInt(kb.readLine());
                for(int i = 0; i<numfinal; i++){
                    tempfinal = tempNFA.getState(kb.readLine());
                    tempNFA.addFinalState(tempfinal);
                }

            //list of NFAs
            nfaList.add(tempNFA);
            if(counter<nummachines-1)
                kb.readLine();
            counter++;
        }
        kb.close();

        Collections.sort(nfaList, Comparator.comparing(NFA::getName).thenComparing(NFA::getName));
        

        for(int i=0; i < nummachines; i++){
            tempDFA = new DFA(nfaList.get(i));
            dfaList.add(tempDFA);
            EquivalentList[i] = new ArrayList<>();
            EquivalentList[i].add(tempDFA.getName());
        } 

        for(int i=0; i < nummachines; i++){
            DFA tempDFA1 = dfaList.get(i);
    
             for(int j=0; j < nummachines; j++){
                DFA tempDFA2 = dfaList.get(j);
                boolean check = tempDFA2.DFAequivalence(tempDFA2, tempDFA1);

                if(check){
                    if(j>i){
                        EquivalentList[i].add(tempDFA2.getName());
                    }
                    else if(j<i){
                        EquivalentList[i].clear();
                        j=nummachines;
                    }
                }
            } 
        } 

        int cluster = 0;
        for (int i = 0; i < nummachines; i++) {
            if(EquivalentList[i].size() > 0){
                cluster++;
            }
        }
        System.out.println(cluster);

        for (int i = 0; i < nummachines; i++) {
            if(EquivalentList[i].size() > 0){
                for (int j = 0; j < EquivalentList[i].size(); j++){
                    System.out.print(EquivalentList[i].get(j) + " ");
                }
                System.out.println();
            }
        }

    }
    }

