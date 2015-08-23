/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.thedreamstudio.game.army;

/**
 * Linh ban tia.
 * 
 * @author truongps
 */
public class Scout extends Soldier {
	public static final int COST = 37;

	public Scout() {
		typeID = Soldier.TYPE_SCOUT;
		name = "Lính tinh nhuệ";
		hp = 120;
		mana = 56;
		damageMin = 52;
		damageMax = 85;
		rangeView = 8;
		costMove = 8;
		rangeAttack = 7;
		costAttack = 26;
		cost = 37;
	}
}
