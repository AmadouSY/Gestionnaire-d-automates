import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/************ CLASSE BIANIRE **************/

/**
 * Representation d'un sous arbre binaire
 */
public class Binaire extends Arbre {

	public Arbre gauche;
	public Arbre droit;

	/**
	 * Construit le noeud binaire
	 * @param symbole symbole
	 * @param gauche fils gauche
	 * @param droit fils droit
	 */
	public Binaire(char symbole, Arbre gauche, Arbre droit){
		this.id = Arbre.ID_COMPT++;
		this.symbole = symbole;
		this.gauche = gauche;
		this.droit = droit;
		this.premiers = new HashSet<Feuille>();
		this.derniers = new HashSet<Feuille>();
		
		if(symbole == '.'){
			this.contientMotVide = gauche.contientMotVide && droit.contientMotVide;
			
			this.premiers.addAll(gauche.premiers);
			if(gauche.contientMotVide) this.premiers.addAll(droit.premiers);
			
			this.derniers.addAll(droit.derniers);
			if(droit.contientMotVide) this.derniers.addAll(gauche.derniers);
		}else{
			this.contientMotVide = gauche.contientMotVide || droit.contientMotVide;
			
			this.premiers.addAll(gauche.premiers);
			this.premiers.addAll(droit.premiers);

			this.derniers.addAll(gauche.derniers);
			this.derniers.addAll(droit.derniers);
		}
	}
	
	/**
	 * Clone le noeud binaire
	 * @return le clone
	 */
	public Binaire clone(){
		return new Binaire(this.symbole, this.gauche.clone(), this.droit.clone());
	}
	
	/**
	 * Teste si les deux arbres sont egaux
	 * @param a arbre a comparer
	 * @return resultat du test
	 */
	public boolean compare(Arbre a){
		if(symbole == a.symbole){
			Binaire k = (Binaire)a;
			return gauche.compare(k.gauche) && droit.compare(k.droit);
		}
		return false;
	}
	
	/**
	 * Residuel du noeud Binaire
	 * @param c lettre du residuel
	 * @return le residuel
	 */
	public Arbre residuelSousArbre(char c){
		Arbre g, d;
		if(this.symbole == '.'){
			if(gauche.contientMotVide){
				g = gauche.residuelSousArbre(c);
				d = droit.clone();
				if(g == null || d == null) g = null;
				else g = new Binaire('.', g, d);
				d = droit.residuelSousArbre(c);
				if(g == null && d == null) return null;
				if(g == null) return d;
				if(d == null) return g;
				return new Binaire('+', g, d);
			}
			g = gauche.residuelSousArbre(c);
			d = droit.clone();
			if(g == null || d == null) return null;
			return new Binaire('.', g, d);
		}else{
			g = gauche.residuelSousArbre(c);
			d = droit.residuelSousArbre(c);
			if(g == null && d == null) return null;
			if(g == null) return d;
			if(d == null) return g;
			return new Binaire('+', g, d);
		}
	}
	
	/**
	 * Supprime les mots vide d'un arbre
	 * @return l'arbre sans les mots vides
	 */
	public Arbre delMotVide(){
		gauche = gauche.delMotVide();
		droit = droit.delMotVide();
		if(gauche.symbole == '0' && droit.symbole == '0') return new Feuille('0');
		if(gauche.symbole == '0') return droit;
		if(droit.symbole == '0') return gauche;
		return this;
	}
	
	/**
	 * Recupere l'alphabet de l'arbre
	 * @return ensemble contenant l'alphabet
	 */
	public Set<Character> alphabet(){
		Set<Character> a = new HashSet<Character>();
		a.addAll(gauche.alphabet());
		a.addAll(droit.alphabet());
		return a;
	}
	
	/**
	 * Recupere les successeurs 
	 * @return les successeurs
	 */
	public Map<Feuille, Set<Feuille>> succ(){
		HashMap<Feuille, Set<Feuille>> map = new HashMap<Feuille, Set<Feuille>>();
		map.putAll(gauche.succ());
		map.putAll(droit.succ());
		if(symbole == '.'){
			for(Feuille g : gauche.derniers){
				for(Feuille d : droit.premiers){
					if(map.containsKey(g)){
						Set<Feuille> tmp = map.get(g);
						tmp.add(d);
					}else{
						Set<Feuille> tmp = new HashSet<Feuille>();
						tmp.add(d);
						map.put(g, tmp);
					}
				}
			}
		}
		return map;
	}

	@Override
	public String toString(){
		if(symbole == '.') return "["+gauche.toString()+symbole+droit.toString()+"]";
		else return "("+gauche.toString()+symbole+droit.toString()+")";
	}
	

}
