import tensorflow as tf
import numpy as np


class TensorEvolution:
    def __init__(self, population, survival_rate, fitness_function):
        self.population = population
        self.survival_rate = survival_rate
        self.fitness_function = fitness_function
        self.fitness = fitness_function(self.population)
        self.rate = 0.2
        self.magnitude = 0.3

    def evolve(self):
        order = tf.argsort(self.fitness)
        survivor_size = int(self.survival_rate * self.population.shape[0].value)
        survivors_indices = order[-survivor_size:]
        next_gen_size = self.population.shape[0].value - survivor_size

        next_gen_indices = tf.tile(survivors_indices, [int(np.ceil(next_gen_size / survivor_size))])[:next_gen_size]

        next_gen_pre = tf.gather_nd(self.population, tf.reshape(next_gen_indices, [next_gen_indices.shape[0].value, 1]))

        def f(x): return TensorEvolution.mutate_function(x, self.rate, self.magnitude)
        next_gen = tf.map_fn(f, next_gen_pre)
        survivors = tf.gather_nd(self.population, tf.reshape(survivors_indices, [survivors_indices.shape[0].value, 1]))

        self.population = tf.concat([survivors, next_gen], 0)
        self.fitness = self.fitness_function(self.population)

    @staticmethod
    def mutate_function(x, rate, magnitude):
        if np.random.random() > rate:
            return tf.add(x, tf.constant(magnitude * (np.random.random() * 2 - 1), tf.float32))
        else:
            return x

    @staticmethod
    def single_chromosome(population, fitness_function):
        results = []
        for i in range(population.shape[0].value):
            results.append(tf.reshape(fitness_function(population[i]), [1]))
        return tf.concat(results, 0)
