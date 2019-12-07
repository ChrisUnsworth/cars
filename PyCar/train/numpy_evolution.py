import numpy as np


class NumpyEvolution:
    def __init__(self, population, survival_rate, fitness_function):
        self.population = population
        self.survival_rate = survival_rate
        self.fitness_function = fitness_function
        self.fitness = np.array(fitness_function(self.population, np.full((len(population)), np.NINF)))
        self.rate = 0.5
        self.magnitude = 0.2

    def evolve(self):
        survivor_size = int(self.survival_rate * len(self.population))
        next_gen_size = len(self.population) - survivor_size
        survivors_indices = np.argpartition(self.fitness, next_gen_size)[-survivor_size:]
        next_gen_indices = [survivors_indices[x % survivor_size] for x in range(next_gen_size)]
        next_gen_pre = self.population[next_gen_indices]
        next_gen = [self.mutate_function(x) for x in next_gen_pre]
        survivors = self.population[survivors_indices]
        old_fitness = self.fitness[survivors_indices]

        self.population = np.concatenate([survivors, next_gen])
        self.fitness = np.array(self.fitness_function(self.population,
                                                      np.concatenate([old_fitness, np.full((len(next_gen)), np.NINF)])))

    def mutate_function(self, chromosome):
        return [x + (self.magnitude * (np.random.random() * 2 - 1))
                if np.random.random() > self.rate else x for x in chromosome]

    def best(self, n=1):
        indices = self.fitness.argsort(self.fitness)[-n:]
        return self.population[indices]

    @staticmethod
    def single_chromosome(population, previous, fitness_function):
        return [previous[i] if previous[i] != np.NINF else fitness_function(population[i])
                for i in range(len(population))]
