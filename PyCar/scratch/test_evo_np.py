import numpy as np
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.numpy_network import NumpyNetwork as Network
from train.numpy_evolution import NumpyEvolution


def fitness(chromosome):
    def np_sigmoid(a): return 1 / (1 + np.exp(-a))
    layers = [Layer(2, 5, np_sigmoid), Layer(5, 3, np_sigmoid), Layer(3, 1, np_sigmoid)]

    network = Network(layers)

    n = network.build(chromosome)

    results = [
        n([0, 0]),
        n([1, 0]),
        n([0, 1]),
        n([1, 1])]

    expected = [[0], [1], [1], [0]]

    return sum([1 - np.abs(results[i] - expected[i]) for i in range(len(results))])[0]


print("fitness test: {}".format(fitness(np.random.rand(37))))

pop = np.random.rand(500, 37)


def fit(p): return NumpyEvolution.single_chromosome(p, fitness)


evo = NumpyEvolution(pop, 0.2, fit)

print(max(evo.fitness))

for i in range(1000):
    evo.evolve()
    if i % 20 == 0:
        print(max(evo.fitness))

print(max(evo.fitness))
