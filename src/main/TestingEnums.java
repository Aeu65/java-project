/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;



/**
 *
 * @author 0702arclarebots
 */
public class TestingEnums {

    private enum Bijou {
        RUBY, AMETHYST
    }
    
    private static class Cadeau {
        private Bijou type = Bijou.RUBY;
        private final String name;
        
        public Cadeau(String name) {
            this.name = name;
        }
        
        public void change() {
            if(this.type.equals(Bijou.RUBY)) {
                this.type = Bijou.AMETHYST;
            } else {
                this.type = Bijou.RUBY;
            }
        }

        public Bijou getType() {
            return type;
        }

        public void setType(Bijou b) {
            this.type = b;
        }
        
        @Override
        public String toString() {
            return "Cadeau " + name + " : " + type;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Cadeau a = new Cadeau("a");
        System.out.println(a);
        
        Cadeau b = new Cadeau("b");
        System.out.println(b);
        
        // on change a, son type devient AMETHYST
        a.change();
        System.out.println(a);
        
        // on prend le type de a qu'on met dans le type de b
        b.setType(a.getType());
        System.out.println(b);
        
        // on change le type de a, qui devient ruby
        a.change();
        System.out.println(a);
        
        // et ici on va voir si il y a besoin de deep copy avec les enum
        // b.getType devrait être AMETHYST
        System.out.println(b);
    }
    
}
