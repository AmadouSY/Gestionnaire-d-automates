import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.File;

/************ CLASSE AUTOMATE **************/

/**
 * Representation d'un Automate fini
 */
public class Automate extends EnsEtat {

	private static final long serialVersionUID = 1L;

    private EnsEtat initiaux;
	private Scanner sc;

	private static Moore[] colonnes;

	
	/**
	 * Construit l'automate vide
	 */
    public Automate() {
        super();
        initiaux = new EnsEtat();
    }
    
	/**
	 * Construit l'automate a partir d'un arbre
	 * @param arbre l'arbre
	 */
	public Automate(Arbre arbre){
		this();
		this.fromArbre(arbre);
	}
    
    /**
	 * Construit l'automate a partir d'un fichier
	 * @param fichier fichier contenant l'automate
	 */
    public Automate(String fichier){
    	this();
    	this.readFile(fichier);
    }
    
    /**
     * Construit un automate aleatoire
     * @param nbrEtat nombre d'etats de l'automate
     * @param alphabet alphabet de l'automate
     */
    public Automate(int nbrEtat, HashSet<Character> alphabet){
		this();
		Random rand = new Random(System.currentTimeMillis());
		
		for(int i = 0; i < nbrEtat; i++){
			this.ajouteEtatSeul(new Etat((rand.nextInt(10) < 3)? true: false, (rand.nextInt(10) < 4)? true: false, i));
		}
		
		if(initiaux.isEmpty()) this.getEtat(0).setInit(true);
		if(!this.contientTerminal()) this.getEtat(nbrEtat-1).setTerm(true);
		
		for(Etat etat : this){
			for(Character c : alphabet){
				for(Etat succ : this){
					if(rand.nextInt(10) < 3) etat.ajouteTransition(c, succ);
				}
			}
		}
	}
    
    /**
     * Construit l'automate a partir des residuels
     * @param residuels residuels d'un arbre
     */
    public Automate(ArrayList<Arbre> residuels){
		this();
		HashMap<Arbre, Etat> map = new HashMap<Arbre, Etat>();
		Etat courant = null;
		int compteur = 0;
		
		for(Arbre a : residuels){
			boolean test = false;
			for(Arbre k : map.keySet()){
				if(k.compare(a) && k.residuelTerm == a.residuelTerm){
					test = true;
					break;
				}
			}
			if(!test){
				courant = new Etat(a.residuelInit, a.residuelTerm, compteur++);
				this.ajouteEtatSeul(courant);
				map.put(a, courant);
			}
		}
		
		for(Arbre a : residuels){
			courant = map.get(a);
			for(Map.Entry<Character, Arbre> entre : a.residuels.entrySet()){
				if(entre.getValue() != null){
					for(Map.Entry<Arbre,Etat> succ: map.entrySet()){
						if(entre.getValue().compare(succ.getKey()) && entre.getValue().residuelTerm == succ.getKey().residuelTerm){
							courant.ajouteTransition(entre.getKey().charValue(), succ.getValue());
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Ajoute l'etat a l'automate
	 * @param e etat a ajouter
	 * @return true, si l'etat a ete ajoute
	 */
	public boolean ajouteEtatSeul(Etat e){
		if(!this.add(e)) return false;
		if(e.isInit()) initiaux.add(e);
		return true;
	}

    public boolean ajouteEtatRecursif(Etat e){
    	
    	if(!ajouteEtatSeul(e)) return false;
    	else {
    		EnsEtat succ = e.succ();
    		if(succ != null){
    			for(Etat ee : succ){
    				ajouteEtatRecursif(ee);
    			}
    		}
    		return true;
    		
    	}
    	
    }

	/**
	 * Teste si l'automate est deterministe
	 * @return true, si l'automate est deterministe
	 */
	public boolean estDeterministe(){
		if(this.isEmpty()) return true;

		if(this.initiaux.size() > 1) return false;
		
		for(Etat e : this){
			for(Character a : e.getTransitions().keySet()){
				if(e.succ(a).size() > 1){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Determinise l'automate
	 * @return l'automate deterministe
	 */
    public Automate determinise(){
    	
    	//Contient le nouvel automate deterministe
    	Automate adet = new Automate();
    	
    	//Collection pour stocké les Etat en fonction des ensemble d'Etat
    	HashMap<EnsEtat, Etat> m = new HashMap<EnsEtat, Etat>();
    	
    	//Pile des ensemble d'etat
    	Stack<EnsEtat> p = new Stack<EnsEtat>();
    	
    	//On initialise la pile avec les etats initiaux
    	p.push(this.initiaux);
    	
    	
    	boolean init = false, term = false;
		if(this.initiaux.contientTerminal()) term = true;
		
		//Declaration d'un nouvel etat pour l'état deterministe
		Etat ini = new Etat(true,term,0);
		
		//Ajout de cette etat a la collection qui a pour clé les etat initiaux
		m.put(this.initiaux, ini);
		
		//Compteur
    	int cpt = 1;
    	
    	//Boucle
    	while(!p.isEmpty()){
    		
    		//On dépile
    		EnsEtat tmp = p.pop();
    		
    		//On récupère l'état de l'automate deterministe 
    		Etat courant = (Etat) m.get(tmp);
    		adet.ajouteEtatRecursif(courant);
    		
    		//On parcours toute les lettre de l'alphabet
    		for(Character c : tmp.alphabet()){
    			
    			//On récupère l'ensemble des etat sameseur pour une lettre donné
    			EnsEtat succ_tmp = tmp.succ(c);
    			
    			if(succ_tmp != null){
    				if(!m.containsKey(succ_tmp)){
    					
    					init = false; term = false;
    					if(succ_tmp.contientTerminal()) term = true;
    					
    					//Création d'un nouvel etat
    					Etat e_tmp = new Etat(init, term, cpt);
    					
    					//On empile 
    					p.push(succ_tmp);
    					m.put(succ_tmp, e_tmp);
    					cpt++;
    					//courant.ajouteTransition(c,e_tmp);
    				}
    				courant.ajouteTransition(c, (Etat)m.get(succ_tmp));
    			}
    			
    			
    			
    		}
    		 
    		
    	}
    	
    	return adet;
    		
    	
    }
	

	/**
	 * Minismise l'automate
	 * @return l'automate minimal
	 */
	public Automate minimisation(){
		if(this.estDeterministe()) 
			return this.minimisationAutomate();
		else 
			return this.determinise().minimisationAutomate();
		
	}

	
    /**
	 * Minimisation de l'automate
	 * @param A automate a minimiser
	 * @return l'automate minimal
	 * @see Moore
	 */
	public Automate minimisationAutomate(){
		
		// Etape 1: tableau de Moore
		
		//Verifie s'il s'agit du premier appel
		boolean premierAppel = true;
		
		//
		char [] alphabet = new char[this.alphabet().size()];
		colonnes = new Moore[this.size()];
		
		//On remplit alphabet avec l'alphabet de l'automate
		int compteur = 0;
		for(Character c : this.alphabet()) 
			alphabet[compteur++] = c.charValue();
		
		//On remplit colonnes avec les differents le Moor des etat de l'automate
		compteur = 0;
		for(Etat e : this)
			colonnes[compteur++] = new Moore(e, alphabet.length);
				
		//Tant que Moore n'est pas fini
		do{
			//On verifie s'il s'agit du premier appel
			if(premierAppel) premierAppel = false;
			//Si ce n'est pas le cas on prepare l'etape
			else{
				for(Moore etape : colonnes){
					etape.debut = etape.bilan;
				}
			}
			
			//On on affecte une valeur a chaque etat
			for(Moore etape : colonnes){
				for(int j = 0; j < alphabet.length; j++){
					
					//Etat suivant par la lettre
					EnsEtat succ = etape.etat.succ(alphabet[j]);
					
					// S'il la transition n'existe pas 
					if(succ.isEmpty()){
						//La transition est fausse
						etape.transitions[j] = -1;
					}else{
						Etat suivant = succ.iterator().next();
						Moore etapeDuSuivant = null;
						for(Moore e : colonnes){
							if(e.etat == suivant){
								etapeDuSuivant = e;
								break;
							}
						}
					
						if(etapeDuSuivant == null) {
							etape.transitions[j] = -1;
							break;
						}else
							etape.transitions[j] = etapeDuSuivant.debut;
					}
				}
			}
			
			//On realise le bilan
			compteur = 1;
			boolean same = false;
			for(int i = 0; i < colonnes.length; i++){
				for(int j = 0; j < i; j++){
					
					boolean test = true;
					
					if(colonnes[i].debut != colonnes[j].debut) 
						test = false;
					for(int k = 0; k < colonnes[i].transitions.length; k++){
						if(colonnes[i].transitions[k] != colonnes[j].transitions[k]) 
							test = false;
					}
					
					if(test){
						colonnes[i].bilan = colonnes[j].bilan;
						same = true;
						break;
					}
				}
				if(!same) colonnes[i].bilan = compteur++;
				same = false;
			}
									
		}while(!arret());
	
		//Etape 2: Construction de l'automate
		
		
		//Contient la liste des indices similaires a un indice infÃ©rieur
		HashSet<Integer> memeEtat = new HashSet<Integer>();
		
		//Recupere les indices de colonnes qui apres un bilan qui sont similaires
		HashSet<Integer> tmp = new HashSet<Integer>();
		for(int i = 0; i < colonnes.length; i++){
			//Si les valeurs sont les meme
			if(!tmp.add(new Integer(colonnes[i].bilan)))
				//On ajoute l'indice a memeEtat
				memeEtat.add(new Integer(i));
		}
		
		
		Automate Am = new Automate();
		
		// On ajoute les etats de l'automate
		for(int i = 0; i < colonnes.length; i++){
			Moore etape = colonnes[i];
			//Si l'etat n'est pas un doublon
			if(!memeEtat.contains(Integer.valueOf(i)))
				//On l'ajoute a l'automate minimal
				Am.ajouteEtatSeul(new Etat(etape.etat.isInit(), etape.etat.isTerm(), etape.bilan));
		}
		
		//
		for(int i = 0; i < colonnes.length; i++){
			Moore etape = colonnes[i];
			//Si l'etat n'est pas un doublon
			if(!memeEtat.contains(Integer.valueOf(i))){
				
				for(int j = 0; j < alphabet.length; j++){
					//Si l'etat admet une transition par la lettre
					if(etape.transitions[j] != -1){
						//On ajoute les transition a l'automate
						Etat etat = Am.getEtat(etape.bilan);
						etat.ajouteTransition(alphabet[j], Am.getEtat(etape.transitions[j]));
					}
				}
			}
		}
		
		//On renvoie l'automate minimal
		return Am;
	}
	
	/**
	 * Teste si 
	 * @return
	 */
	private boolean arret(){
		for(Moore etape : colonnes){
			if(etape.debut != etape.bilan) return false;
		}
		return true;
	}
	
	/**
	 * Arbre ----> Automate
	 * @param arbre arbre d'une expression rationnelle
	 */
	private void fromArbre(Arbre arbre){
		initiaux.clear();
		this.clear();
		
		int idCompteur = 0;
		HashMap<Feuille, Etat> map = new HashMap<Feuille, Etat>();
		Map<Feuille, Set<Feuille>> succ = arbre.succ();
		Stack<Feuille> pile = new Stack<Feuille>();
		
		Etat etatInit = new Etat(true, arbre.contientMotVide, idCompteur++);
		Feuille feuilleInit = new Feuille('0');
		this.ajouteEtatSeul(etatInit);
		succ.put(feuilleInit, arbre.premiers);
		map.put(feuilleInit, etatInit);
		pile.push(feuilleInit);
		
		while(!pile.isEmpty()){
			Feuille feuilleCourante = pile.pop();
			if(succ.get(feuilleCourante) != null){
				for(Feuille feuilleSucc : succ.get(feuilleCourante)){
					Etat etatCourant = map.get(feuilleSucc);
					if(etatCourant == null){
						etatCourant = new Etat(false, arbre.derniers.contains(feuilleSucc), idCompteur++);
						this.ajouteEtatSeul(etatCourant);
						map.put(feuilleSucc, etatCourant);
						pile.push(feuilleSucc);
					}
					map.get(feuilleCourante).ajouteTransition(feuilleSucc.symbole, etatCourant);
				}
			}
		}
	}
	
	/**
	 * Sauvegarde l'automate dans un fichier
	 * @param fichier nom du fichier
	 */
	public void toFile(String fichier){
		try{
			new PrintStream(fichier).println(this.toString());
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Extrait l'automate d'un fichier
	 * @param fichier nom du fichier contenant l'automate
	 */
	public void readFile(String fichier){
		try{
    		sc = new Scanner(new File(fichier));
    		String ligne = sc.nextLine();
    		String[] ligneSplit = ligne.split(" ");
    		int etats = Integer.parseInt(ligneSplit[0]);
    		for(int  i = 0; i < etats; i++){
    			this.add(new Etat(false, false, i));
    		}
    		
    		Etat etatTmp = null;
    		while(sc.hasNextLine()){
    			ligne = sc.nextLine();
    			
    			if(ligne != null && ligne.length() > 0){
    				ligneSplit = ligne.split(" ");
    				etatTmp = getEtat(Integer.parseInt(ligneSplit[0]));
    				etatTmp.setInit(ligneSplit.length > 1 && ligneSplit[1].equals("initial"));
    				etatTmp.setTerm(ligneSplit.length > 1 && ligneSplit[1].equals("terminal") || ligneSplit.length > 2 && ligneSplit[2].equals("terminal"));
    				if(etatTmp.isInit()) initiaux.add(etatTmp);
    				
    				do{
    					ligne = sc.nextLine();
    					if(ligne == null || ligne.length() == 0) break;
    					ligneSplit = ligne.split(" ");
    					char transition = ligneSplit[0].charAt(0);
    					for(int i = 1; i < ligneSplit.length; i++){
    						etatTmp.ajouteTransition(transition, getEtat(Integer.parseInt(ligneSplit[i])));
    					}
    				}while(sc.hasNextLine());
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * Test si deux automates sont egaux
     * @param test l'automate auquel le comparer
     * @return resultat du test
     */
    public boolean compare(Automate test){
		if(this.size() != test.size()){
			return false;
		}
		
		if(!this.alphabet().equals(test.alphabet())){
			return false;
		}
		
		HashMap<Etat, Etat> map = new HashMap<Etat, Etat>();
		ArrayList<Etat> liste = new ArrayList<Etat>();
		Stack<Etat> pile = new Stack<Etat>();
		Set<Character> alphabet = this.alphabet();
		
		if(this.initiaux.size() != 1 || test.initiaux.size() != 1){
			return false;
		}
		
		pile.push(this.initiaux.iterator().next());
		map.put(this.initiaux.iterator().next(), test.initiaux.iterator().next());
		
		while(!pile.isEmpty()){
			Etat courant = pile.pop();
			Etat lie = map.get(courant);
			for(Character c : alphabet){
				EnsEtat ensSucc = courant.succ(c);
				EnsEtat ensLieSucc = lie.succ(c);
				if(ensSucc.size() > 0 && ensLieSucc.size() > 0){
					if(ensSucc.size() != 1 || ensLieSucc.size() != 1){
						return false;
					}
					Etat courantSucc = ensSucc.iterator().next();
					if(!liste.contains(courantSucc)){
						Etat lieSucc = ensLieSucc.iterator().next();
						liste.add(courantSucc);
						map.put(courantSucc, lieSucc);
						pile.push(courantSucc);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Recupere les etats initiaux
	 * @return les etats initiaux
	 */
    public EnsEtat getInitiaux() {
        return initiaux;
    }
}
