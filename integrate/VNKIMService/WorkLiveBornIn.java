package org.me.VNKIMService;

import BayesianNetworks.*;

class WorkLiveBornIn extends BayesNet {
    public WorkLiveBornIn() {
        name = "WorkLiveBornIn";

        DiscreteVariable person =
            new DiscreteVariable("person",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable location =
            new DiscreteVariable("location",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable organization =
            new DiscreteVariable("organization",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable born =
            new DiscreteVariable("born",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable live =
            new DiscreteVariable("live",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable work =
            new DiscreteVariable("work",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable in =
            new DiscreteVariable("in",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});

        DiscreteVariable born_in =
            new DiscreteVariable("born_in",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});
        DiscreteVariable work_in =
            new DiscreteVariable("work_in",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});
        DiscreteVariable live_in =
            new DiscreteVariable("live_in",
                                    DiscreteVariable.CHANCE,
                                    new String[] {"true","false"});
        
        DiscreteFunction p1 =
            new DiscreteFunction(
                new DiscreteVariable[] {person},
                new DiscreteVariable[] {},
                new double[] {0.2335, 0.7665});

        DiscreteFunction p2 =
            new DiscreteFunction(
                new DiscreteVariable[] {location},
                new DiscreteVariable[] {},
                new double[] {0.1569, 0.8431});

        DiscreteFunction p3 =
            new DiscreteFunction(
                new DiscreteVariable[] {organization},
                new DiscreteVariable[] {},
                new double[] {0.0192, 0.9808});
        
        DiscreteFunction p4 =
            new DiscreteFunction(
                new DiscreteVariable[] {born},
                new DiscreteVariable[] {},
                new double[] {0.0807, 0.9193});

        DiscreteFunction p5 =
            new DiscreteFunction(
                new DiscreteVariable[] {live},
                new DiscreteVariable[] {},
                new double[] {0.0289, 0.9711});

        DiscreteFunction p6 =
            new DiscreteFunction(
                new DiscreteVariable[] {work},
                new DiscreteVariable[] {},
                new double[] {0.0221, 0.9779});

        DiscreteFunction p7 =
            new DiscreteFunction(
                new DiscreteVariable[] {in},
                new DiscreteVariable[] {},
                new double[] {0.4587, 0.5413});
        
        DiscreteFunction p8 =
            new DiscreteFunction(
                new DiscreteVariable[] {born_in},
                new DiscreteVariable[] {person, location, born, in},
                new double[] {0.97, 0.03, 0.0, 0.0, 0.88, 0.12, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.03, 0.97, 1.0, 1.0, 0.12, 0.88, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0});

        DiscreteFunction p9 =
            new DiscreteFunction(
                new DiscreteVariable[] {live_in},
                new DiscreteVariable[] {in, person, location, live},
                new double[] {0.9857, 0.1957, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0109, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0143, 0.8043, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.9891, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0});

        DiscreteFunction p10 =
            new DiscreteFunction(
                new DiscreteVariable[] {work_in},
                new DiscreteVariable[] {person, location, work, organization, in},
                new double[] {0.95, 0.122, 0.95, 0.0, 0.2051, 0.0, 0.0, 0.0, 1.0, 0.122, 0.0, 0.0, 0.2051, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05, 0.878, 0.05, 1.0, 0.7949, 1.0, 1.0, 1.0, 0.0, 0.878, 1.0, 1.0, 0.7949, 1.0, 1.0, 1.0, 1.0 ,1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0});
        
        add( new DiscreteVariable[] 
             { person, location, organization, born, live, work, in, born_in, live_in, work_in } );

        add( new DiscreteFunction[] { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 } );
    }
}

