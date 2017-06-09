import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

/************ CLASSE ARBRE **************/

/**
 * Representation basique d'un arbre
 */
public abstract class Arbre {

	public static int ID_COMPT = 0;

	public int id;
	public char symbole;

	public boolean contientMotVide;
	public Set<Feuille> premiers;
	public Set<Feuille> derniers;
	
	public HashMap<Character, Arbre> residuels;
	
	public boolean residuelInit = false;
	public boolean residuelTerm = false;

	public abstract Map<Feuille, Set<Feuille>> succ();
	public abstract Arbre clone();
	public abstract boolean compare(Arbre a);
	public abstract Arbre delMotVide();
	public abstract Set<Character> alphabet();
	public abstract Arbre residuelSousArbre(char c);

	
	/**
	 * Expression rationnelle -----> arbre
	 * @param expr expression rationnelle
	 * @return l'arbre de l'expression rationnelle
	 */
	public static Arbre lirePostfixe(String expr){
		Stack<Arbre> pile = new Stack<Arbre>();
		
		for(int i = 0; i < expr.length(); i++){
			char valeur = expr.charAt(i);
			if(valeur == '*'){
				Arbre element = pile.pop();
				Unaire unaire = new Unaire(element);
				pile.push(unaire);
			}else if(valeur == '.' || valeur == '+'){
				Arbre element1 = pile.pop();
				Arbre element2 = pile.pop();
				Binaire binaire = new Binaire(valeur, element2, element1);
				pile.push(binaire);
			}else{
				Feuille f = new Feuille(valeur);
				pile.push(f);
			}
		}

		return pile.pop();
	}
	

	
	/**
	 * Residuel de l'arbre par la lettre passee en argument
	 * @param c lettre du residuel
	 * @return le residuel
	 */
	public Arbre residuel(char c){
		boolean deb = false;
		Feuille F = new Feuille(c);
		
		// On verifie que l'on peut commencer par c
		for(Arbre a : premiers)
			if(a.compare(F))
				deb = true;
		
		//S'il ne peut pas on s'arrete
		if(!deb) return null;

		Arbre a = this.residuelSousArbre(c);
				
		if(a!=null){
			boolean fin = false;
			if(a.symbole == '+'){
				Binaire nb = (Binaire) a;
				fin = (nb.droit.symbole == '0' || nb.gauche.symbole == '0');
			}else if(a.symbole == '*' || a.symbole == '0'){
				fin = true;
			}
			a = a.delMotVide();
			if(a.symbole == '*')
				fin = true;
			a.residuelTerm = fin;
		}
		return a;
	}

	
	/**
	 * Stocke tous les résiduels de l'arbre en argument dans une liste
	 * @param a arbre dont il faut les résisduels
	 * @return liste des résiduels
	 */
	public static ArrayList<Arbre> residuels(Arbre a){
		ArrayList<Arbre> listResiduels = new ArrayList<Arbre>();
		Stack<Arbre> pile = new Stack<Arbre>();
		Set<Character> alphabet = a.alphabet();
		
		pile.push(a);
		listResiduels.add(a);
		a.residuelInit = true;
		
		while(!pile.empty()){
			Arbre depile = pile.pop();
			depile.residuels = new HashMap<Character, Arbre>();
			for(Character c : alphabet){
				Arbre res = depile.residuel(c.charValue());
				depile.residuels.put(c, res);
				if(res != null){
					boolean test = false;
					for(Arbre k : listResiduels){
						if(k.compare(res) && k.residuelTerm == res.residuelTerm){
							test = true;
							break;
						}
					}
					if(!test){
						pile.push(res);
						listResiduels.add(res);
					}
				}
			}
		}
		return listResiduels;
	}
}


