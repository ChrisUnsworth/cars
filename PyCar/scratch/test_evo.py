import tensorflow as tf
import numpy as np
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.tensor_network import TensorNetwork as Network
from train.tensor_evolution import TensorEvolution


def fitness(chromosome):
    layers = [Layer(2, 5, tf.nn.sigmoid), Layer(5, 3, tf.nn.sigmoid), Layer(3, 1, tf.nn.sigmoid)]

    network = Network(layers)

    huh = [evaluate(network, chromosome, [0, 0]),
           evaluate(network, chromosome, [1, 0]),
           evaluate(network, chromosome, [0, 1]),
           evaluate(network, chromosome, [1, 1])]

    results = tf.concat(huh, 0)

    expected = tf.constant([[0], [1], [1], [0]], tf.float32)

    return tf.reduce_sum(tf.subtract(tf.ones([4, 1]), tf.abs(tf.subtract(results, expected))))


def evaluate(network, chromosome, x):
    result = network.build_tensor(tf.constant(x, tf.float32), chromosome)
    return result


def test_finess():
    chromo = tf.constant(np.random.rand(37), tf.float32)
    test = fitness(chromo)
    sess = tf.Session()
    print(sess.run(test))
    sess.close()


vals = list([list(np.random.rand(37)) for _ in range(1000)])
pop = tf.constant(vals, tf.float32)


def fit(p): return TensorEvolution.single_chromosome(p, fitness)


evo = TensorEvolution(pop, 0.2, fit)

sess = tf.Session()

print(sess.run(tf.reduce_max(evo.fitness)))

for _ in range(100):
    evo.evolve()
    sess.run(evo.population)
    print(sess.run(tf.reduce_max(evo.fitness)))

sess.close()
