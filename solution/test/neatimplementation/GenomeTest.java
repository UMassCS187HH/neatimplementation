package neatimplementation;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class GenomeTest
{
    Genome genome1, genome2;
    static double c1 = 1.0,
                  c2 = 0.8,
                  c3 = 0.4;

    @Before
    public void before()
    {
        this.genome1 = Static.testGenome();
    }
    
    @Test
    public void testPropogate()
    {
        double[] inputs = new double[] {1.0, 0.5};
        double[] outputs = genome1.propogate(new double[] {1.0, 0.5});
        double[] expectedOutputs = new double[2];
        double node4Activation = Static.sigmoid(1*inputs[0]-2*inputs[1]);
        System.out.println(node4Activation);
        double node2Activation = Static.sigmoid(-1.0*node4Activation-0.01*inputs[1]);
        double node3Activation = Static.sigmoid(0.1*node4Activation);
        expectedOutputs[0] = node2Activation;
        expectedOutputs[1] = node3Activation;
        assertEquals("Must have 2 outputs", outputs.length, 2);
        assertTrue("First output is wrong: should be " + expectedOutputs[0] + ", but is " + outputs[0], Math.abs((outputs[0]-expectedOutputs[0])) < 0.001);
        assertTrue("Second output is wrong: should be " + expectedOutputs[1] + ", but is " + outputs[1], Math.abs((outputs[1]-expectedOutputs[1])) < 0.001);
    }
    
    @Test
    public void testClone()
    {
        genome2 = genome1.clone();
        double[] input = new double[] {0.5, 1.0};
        double[] output1 = genome1.propogate(input);
        double[] output2 = genome2.propogate(input);
        assertTrue("Cloned genomes should have same output for same inputs", Arrays.equals(output1, output2));
        genome1.connectionGenes.add(new ConnectionGene(0, 3, 0.01, true, 6));
        genome2.connectionGenes.add(new ConnectionGene(0, 3, 0.01, true, 6));
        output1 = genome1.propogate(input);
        output2 = genome2.propogate(input);
        assertTrue("Cloned genomes with same connection added should have same output for same inputs", Arrays.equals(output1, output2));
    }
    
    @Test
    public void testDistanceIdentical()
    {
        genome2 = genome1.clone();
        double distance = genome1.distance(genome2, c1, c2, c3);
        assertTrue("Distance between two identical genomes should be 0, but was " + distance, distance == 0.0);
    }
    
    /**
     * Tests the distance function when there are excess genes.
     */
    @Test
    public void testDistanceExcessGenes()
    {
        genome2 = genome1.clone();
        genome2.connectionGenes.add(new ConnectionGene(1, 3, 1, true, 5));
        double distance, expectedDistance;
        distance = genome1.distance(genome2, c1, c2, c3);
        expectedDistance = c1 * 1.0 / 6.0;
        assertTrue(distance == expectedDistance);
    }
    
    /**
     * Tests the distance function when there are disjoint and excess genes.
     */
    @Test
    public void testDistanceDisjointAndExcessGenes()
    {
        genome2 = genome1.clone();
        genome2.connectionGenes.add(new ConnectionGene(1, 3, 1, true, 5));
        genome2.connectionGenes.add(new ConnectionGene(0, 2, 1.0, true, 6));
        genome1.connectionGenes.add(new ConnectionGene(0, 3, 0.3, true, 7));
        double distance, expectedDistance;
        distance = genome2.distance(genome1, c1, c2, c3);
        expectedDistance = (c1 * 1 + c2 * 2) / 7;
        assertTrue(distance == expectedDistance);
    }
    
    /**
     * Tests the distance function when there are disjoint genes, different
     * weights, and excess genes.
     */
    @Test
    public void testDistance()
    {
        before();
        testDistanceDisjointAndExcessGenes(); // for setup purposes
        System.out.println("testDistance");
        double distance, expectedDistance;
        genome1.connectionGenes.get(0).weight = 1.0;
        genome2.connectionGenes.get(0).weight = -1.0;
        genome1.connectionGenes.get(2).weight = 0.4;
        genome2.connectionGenes.get(2).weight = 1.4;
        distance = genome2.distance(genome1, c1, c2, c3);
        expectedDistance = (c1 * 1 + c2 * 2) / 7 + c3 * (3.0 / 5.0);
        assertTrue("Error, expected distance " + expectedDistance + " but got distance " + distance, expectedDistance == distance);
    }
    
    /**
     * Tests if the wouldMakeRecurrent method works properly
     */
    @Test
    public void wouldMakeRecurrentTest()
    {
        before();
        assertTrue(genome1.wouldMakeRecurrent(new ConnectionGene(3, 3, 0, true, 10)));
        assertTrue(genome1.wouldMakeRecurrent(new ConnectionGene(2, 4, 0, true, 10)));
        assertFalse(genome1.wouldMakeRecurrent(new ConnectionGene(2, 3, 0, true, 10)));
        assertFalse(genome1.wouldMakeRecurrent(new ConnectionGene(4, 3, 0, true, 10)));
        assertFalse(genome1.wouldMakeRecurrent(new ConnectionGene(3, 2, 0, true, 10)));
        genome1.connectionGenes.add(new ConnectionGene(2, 3, 0, true, 10));
        assertTrue(genome1.wouldMakeRecurrent(new ConnectionGene(3, 2, 0, true, 10)));
    }
}
