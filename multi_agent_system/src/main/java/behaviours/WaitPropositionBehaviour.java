package main.java.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

import main.java.Proposition;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class WaitPropositionBehaviour extends OneShotBehaviour{

        public WaitPropositionBehaviour(Agent a){
            super(a);
        }

        @Override
        public void action() {
            ACLMessage reply = null;
                while (reply == null){
                    reply = myAgent.receive();
                }
                System.out.println("Received message from agent : "
                + reply.getSender().getName() + " > "
                + "\n" + reply.getContent().toString());
        }
        @Override public int onEnd() {return 0; }
    }
