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
		int action;

		//INFORMATION

		//Attacker information
		Attacker attacker =  game.getAttacker();
		Node attLocation = attacker.getLocation();

        //Defenders information
        List<Defender> defenderList = game.getDefenders();
        Actor closestActor = attacker.getTargetActor(defenderList, true);
        Defender closestDefender = (Defender) closestActor;
        Node defLocation = closestDefender.getLocation();
        int defDistance = attLocation.getPathDistance(defLocation);

		//Pill information (normal and power)
		List<Node> pList = game.getPillList();
		List<Node> ppList = game.getPowerPillList();
		Node nearestPill = attacker.getTargetNode(pList, true);


		//1. If there is an invulnerable defender nearby, pursue the closest power pill if possible and safe to do so
		if ((defDistance < 5) && (!(closestDefender.isVulnerable()))){
			int defDirection = attacker.getNextDir(defLocation, true);

			//a. If power pills are available, pursue the power pills while avoiding defenders
			if (ppList.size() != 0) {
				Node closestPP = attacker.getTargetNode(ppList, true);
				int ppDirection = attacker.getNextDir(closestPP, true);
				int ppGap = attLocation.getPathDistance(closestPP);

				//i. If our attacker was waiting at a power pill, eat it
				if ((ppDirection == defDirection) && (ppGap == 1)){
					action = attacker.getNextDir(closestPP, true);
				}

				//ii. Otherwise, if a defender is blocking the way to a power pill, avoid them
				else if (ppDirection == defDirection){
					action = attacker.getNextDir(defLocation, false);
				}

				//iii. Finally, if the path to a power pill is clear, eat the power pill
				else {
					action = attacker.getNextDir(closestPP, true);
				}
			}

			//b. If there are no power pills (available), pursue normal pills while avoiding defenders
			else {
				int pDirection = attacker.getNextDir(nearestPill, true);

				//i. If a defender is blocking the way to a normal pill, avoid them
				if (pDirection == defDirection){
					action = attacker.getNextDir(defLocation, false);
				}

				//ii. Otherwise, if the path to a normal pill is clear, eat the pill
				else {
					action = attacker.getNextDir(nearestPill, true);
				}
			}
		}

		//2. Otherwise, if there is a defender relatively close by and they're vulnerable, hunt them down
		else if ((defDistance < 95) && (closestDefender.isVulnerable())){
			action = attacker.getNextDir(defLocation, true);
		}

		//3. Finally, if the defenders are too far away, eat normal pills and wait at power pills
		else {
			action = attacker.getNextDir(nearestPill, true);

			//a. If our attacker finds a power pill, wait beside it until there are defenders nearby to hunt
			if (ppList.size() != 0){
				Node closestPP = attacker.getTargetNode(ppList, true);
				int ppGap = attLocation.getPathDistance(closestPP);
				if (ppGap <= 1){
					action = attacker.getReverse();
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


