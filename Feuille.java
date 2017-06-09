import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/************ CLASSE FEUILLE **************/

/**
 * Representation d'une feuille d'arbre
 */
public class Feuille extends Arbre{

	/**
	 * Construit la feuille sur le symbole
	 * @param symbole symbole de la feuille
	 */
	public Feuille(char symbole){
		this.id = Arbre.ID_COMPT++;
		this.symbole = symbole;
		this.contientMotVide = symbole == '0';
		this.premiers = new HashSet<Feuille>();
		if(!this.contientMotVide) this.premiers.add(this);
		this.derniers = new HashSet<Feuille>();
		if(!this.contientMotVide) this.derniers.add(this);
	}
	
	/**
	 * Clone la feuille
	 * @return le clone
	 */
	public Feuille clone(){
		return new Feuille(this.symbole);
	}
	
	/**
	 * Test si deux feuilles sont equivalentes
	 * @param a arbre a comparer
	 * @return resultat du test
	 */
	public boolean compare(Arbre a){
		return symbole == a.symbole;
	}
	
	/**
	 * Residuel de la feuille
	 * @param c lettre du residuel
	 * @return le residuel
	 */
	public Arbre residuelSousArbre(char c){
		if(symbole == c) return new Feuille('0');
		else return null;
	}
	
	/**
	 * Supression des mots vides
	 * @return l'arbre sans les mots vides
	 */
	public Arbre delMotVide(){
		return this;
	}
	
	/**
	 * Recupere l'alphabet de l'arbre
	 * @return ensemble contenant l'alphabet
	 */
	public Set<Character> alphabet(){
		Set<Character> a = new HashSet<Character>();
		if(symbole != '0') a.add(symbole);
		return a;
	}

	/**
	 * Recupere les successeurs
	 */
	public Map<Feuille, Set<Feuille>> succ(){
		return new HashMap<Feuille, Set<Feuille>>();
	}
	
	@Override
	public String toString(){
		return ""+symbole;
	}

}
