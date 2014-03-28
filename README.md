# Gridworld-NNGP
A Gridworld<sup>1</sup> Critter (named FlowerHunter) that hunts Flowers by using an artificial neural network (ANN) to make decisions. A genetic algorithm is used to improve the ANNs.

<sup>1</sup> Gridworld is a Java program used to teach AP Computer Science to highschool students. It gives students the chance to work with a relatively large codebase without having to write a whole bunch of code themselves.

## The FlowerHunter Critter
FlowerHunters are able to sense the immediate 8 spaces around them. They use their brains (ANNs) to decide where to move (they can only move to blank spots or spots with Flowers). All decisions are made by their brains (they have no hardcoded motivation to move towards Flowers).

## The Artificial Neural Network
The ANN consists of an input layer (inputs are Doubles), a configurable number of hidden layers (size of each layer is configurable as well), and an output layer. This is a boolean ANN, meaning each neuron outputs a 1 or a 0. Currently the neurons have no activation function; each neuron simply fires (outputs a 1) if the weighted sum of all its connected neurons is greater than its threshold. This means the output layer is a bunch of zeroes and ones, but it can easily be interpreted as a list of Integers or Strings.

## FlowerHunter Brains
Each FlowerHunter has an ANN for a brain. There are 25 inputs in the current implementation: for each of the 8 possible neighboring locations, 1 x coord, 1 y coord, and a number signifying whether or not the Actor (or null) at this location is a Flower. This makes 8*3 = 24 inputs, and the 25th input is the output of Java's Math.random() to vary the behavior each time. Each FlowerHunter begins with randomly assigned weights and thresholds, so initially, their brains aren't very good at figuring out how to eat Flowers. There are 3 outputs because 3 outputs -> 3 bits -> 8 possibilities -> which of the 8 neighboring locations to choose (this explanation is incomplete so look at the implementation for specifics).

## The Genetic Algorithm
This program is essentially a cartesian genetic programmer, except instead of many different function nodes, there's just one (weighted sum function in the neurons of the ANN).

When the program starts (Generation #1), 5 random FlowerHunters (random brains) are created. Each FlowerHunter is placed on a grid randomly populated with Flowers and allowed to act a specific number of times. Once it's finished, the number of Flowers consumed is computed and treated as that FlowerHunter's score. This process is repeated dozens and dozens of times for each FlowerHunter in order to calculate an accurate average score. From these initial 5 FlowerHunters, the highest scoring one is chosen as the seed for the next generation. It is taken and mutated (the weights and thresholds of its brain are changed slightly) 4 times to produce the next generation of 5 FlowerHunters. This process is repeated every time the user clicks the "step" button.

The fact that only FlowerHunters that score well are chosen means there's an environmental pressure to score well. Successive generations get better and better scores because only high scoring FlowerHunters surive to the next round. The important point to note here is that the motivation to consume Flowers comes from the *outside*. There's no code within each FlowerHunter that tells it to eat Flowers.

## Next Steps
Currently, there's no way to see the FlowerHunters in action. The hundreds of trials necessary for each generation take milliseconds to run, so clicking the "step" button leaves the Grid in whatever state the last FlowerHunter left it in. This means there's no way to qualitatively describe the behavior of successful FlowerHunters. I'm really intersted in seeing what kinds of strategies the CGP develops. Do brains *always* favor Flowers over empty spots? Do the FlowerHunters move towards the center of the Grid when there's nothing else they can do? The easiest way to answer these questions is to add a super slow-mo version of the program where **each** step of **each** trial of **each** FlowerHunter of **each** generation is rendered individually. Alternatively, the ability to export brains could be added, and then a separate program could be written that's nothing more than a FlowerHunter with that brain.
