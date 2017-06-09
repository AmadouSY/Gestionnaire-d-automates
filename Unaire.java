import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/************ CLASSE UNAIRE **************/

/**
 * Representation d'un sous arbre unaire
 */
public class Unaire extends Arbre {

	public Arbre fils;
	
	/**
	 * Construit un sous arbre unaire
	 */
	public Unaire(){
		this.id = Arbre.ID_COMPT++;
		this.symbole = '*';
		this.fils = null;
	}

	/**
	 * Construit l'arbre unaire avec son fils
	 * @param fils fils de l'arbre
	 */
	public Unaire(Arbre fils){
		this();
		this.fils = fils;
		this.contientMotVide = true;
		this.premiers = new HashSet<Feuille>();
		this.premiers.addAll(fils.premiers);
		this.derniers = new HashSet<Feuille>();
		this.derniers.addAll(fils.derniers);
	}
	
	/**
	 * Clone le sous arbre unaire
	 * @return le clone
	 */
	public Unaire clone(){
		return new Unaire(this.fils.clone());
	}
	
	/**
	 * Test si deux arbres sont egaux
	 * @param a arbre à comparer
	 * @return resultat du test
	 */
	public boolean compare(Arbre a){
		if(symbole == a.symbole){
			Unaire k = (Unaire)a;
			return fils.compare(k.fils);
		}
		return false;
	}
	
	/**
	 * Residuel du sous arbre Unaire
	 * @param c lettre du residuel
	 * @return le residuel
	 */
	public Arbre residuelSousArbre(char c){
		boolean test = false;
		for(Arbre k : premiers){
			if(k.compare(new Feuille(c))){
				test = true;
				break;
			}
		}
		if(test) {
			Arbre f = fils.residuelSousArbre(c);
			Arbre ff = this.clone();
			if(f == null) return null;
			return new Binaire('.', f, ff);
		}
		return null;
	}
	
	/**
	 * Suppression des mots vides
	 * @return l'arbre sans les mots vides
	 */
	public Arbre delMotVide(){
		fils = fils.delMotVide();
		if(fils.symbole == '0') return new Feuille('0');
		return this;
	}
	
	/**
	 * Recupere l'alphabet de l'arbre
	 * @return ensemble contenant l'alphabet
	 */
	public Set<Character> alphabet(){
		return fils.alphabet();
	}
	
	/**
	 * Recupere le successeur
	 * @see Arbre#succ()
	 */
	public Map<Feuille, Set<Feuille>> succ(){
		HashMap<Feuille, Set<Feuille>> map = new HashMap<Feuille, Set<Feuille>>();
		map.putAll(fils.succ());
		for(Feuille feuilleD : derniers){
			for(Feuille feuilleP : premiers){
				if(map.containsKey(feuilleD)){
					map.get(feuilleD).add(feuilleP);
				}else{
					Set<Feuille> tmp = new HashSet<Feuille>();
					tmp.add(feuilleP);
					map.put(feuilleD, tmp);
				}
			}
		}
		return map;	
	}

	@Override
	public String toString(){
		return "{"+fils.toString()+"}"+symbole;
	}
	
}
