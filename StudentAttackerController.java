package edu.ufl.cise.cs1.controllers;
import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game, long timeDue) {
		int action = Game.Direction.EMPTY;

		//Gather all needed information

		//hero information
		Attacker hero =  game.getAttacker();
		Node heroLocation = hero.getLocation();

		//pill information
		List<Node> pList = game.getPillList();
		List<Node> ppList = game.getPowerPillList();
		Node closestPill = hero.getTargetNode(pList, true);



		//defender information
		List<Defender> defenderList = game.getDefenders();
		Actor closestActor = hero.getTargetActor(defenderList, true);
		Defender closestEnemy = (Defender) closestActor;
		Node enemyLocation = closestEnemy.getLocation();
		int enemyGap = heroLocation.getPathDistance(enemyLocation);


		//1. if enemy is super close and not vulnerable, avoid him while pursuing pills
		if ((enemyGap < 5) && (!(closestEnemy.isVulnerable()))){
			int edirection = hero.getNextDir(enemyLocation, true);
			//2. if power pills available, pursue power pills while avoiding enemy
			if (ppList.size() != 0) {
				Node closestPP = hero.getTargetNode(ppList, true);
				int ppdirection = hero.getNextDir(closestPP, true);
				int ppGap = heroLocation.getPathDistance(closestPP);
				//3. if hero is waiting at a power pill, take it
				if ((ppdirection == edirection) && (ppGap == 1)){
					action = hero.getNextDir(closestPP, true);
				}
				//3. else if enemy is standing in the way of power pill, avoid him
				else if (ppdirection == edirection){
					action = hero.getNextDir(enemyLocation, false);
				}
				//3. else if path to power pill is clear, take the power pill
				else {
					action = hero.getNextDir(closestPP, true);
				}
			}
			//2. if no power pills, pursue regular pills while avoiding enemy
			else {
				int pdirection = hero.getNextDir(closestPill, true);
				//3. if enemy is in the way of pill, avoid enemy
				if (pdirection == edirection){
					action = hero.getNextDir(enemyLocation, false);
				}
				//3, if path to the pill is clear, take pill
				else {
					action = hero.getNextDir(closestPill, true);
				}
			}
		}
		//1. else if enemy is somewhat nearby and is vulnerable, hunt him down
		else if ((enemyGap < 90) && (closestEnemy.isVulnerable())){
			action = hero.getNextDir(enemyLocation, true);
		}
		//1. if enemy far away, hunt pills and pause at power-pills
		else {
			action = hero.getNextDir(closestPill, true);
			//2. If hero runs into power-pill, wait for ambush attack
			if (ppList.size() != 0){
				Node closestPP = hero.getTargetNode(ppList, true);
				int ppGap = heroLocation.getPathDistance(closestPP);
				if (ppGap <= 1){
					action = hero.getReverse();
				}
			}
		}

		return action;
	}
}

//		int action = -1;
//
//		int distanceToPP = Integer.MAX_VALUE;
//		Node closestPP = null;
//		for (Node n : game.getPowerPillList()) {
//			int temp = game.getAttacker().getLocation().getPathDistance(n);
//			if (temp < distanceToPP) {
//				closestPP = n;
//				distanceToPP = temp;
//			}
//		}
//		if (closestPP != null) {
//			//THERE IS A POWER PILL! YES!
//			int distanceToDefender = Integer.MAX_VALUE;
//			Node closestDefender = null;
//			for (Defender d : game.getDefenders()) {
//				if (d.getLairTime() <= 0) {
//					int temp = game.getAttacker().getLocation().getPathDistance(d.getLocation());
//					if (temp < distanceToDefender) {
//						closestDefender = d.getLocation();
//						distanceToDefender = temp;
//					}
//				}
//			}
//			if (distanceToDefender <= 5) {
//				//Eat pill
//				action = game.getAttacker().getNextDir(closestPP, true);
//				return action;
//			}
//			else {
//				//Remain still
//				if (distanceToPP <= 6) {
//					action = game.getAttacker().getReverse();
//					return action;
//				}
//				else {
//					action = game.getAttacker().getNextDir(closestPP, true);
//					return action;
//				}
//			}
//			//CHECK THE DISTANCE TO THAT POWER PILL WITH A VALUE (PRE-DEFINED)
//			//CHECK THE DISTANCE TO THE CLOSEST DEFENDER
//		}
//		return action;
//	}
//}
		//<----------

		/*
		for (Defender d: game.getDefenders()) {
			if (d.isVulnerable()) {
				Node defenderLocation = d.getLocation();
				action = game.getAttacker().getNextDir(defenderLocation, true);
				return action;
			}
		}

		for (Node n : game.getPowerPillList()) {
			action = game.getAttacker().getNextDir(n, true);
			return action;
		}
		for (Node n : game.getPillList()) {
			action = game.getAttacker().getNextDir(n, true);
			return action;
		}
		if (action == -1) {
			action = game.getAttacker().getReverse();
		}
		*/


