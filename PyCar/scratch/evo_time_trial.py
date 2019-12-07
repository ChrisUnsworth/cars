import numpy as np

from cars.simple_car import SimpleCar
from cars.simple_car_driver import SimpleCarDriver
from raceWindow import RaceWindow
from tracks.simple_ring import SimpleRing
from tracks.side_eight import SideEight
from tracks.big_c import BigC
from train.numpy_evolution import NumpyEvolution


driver = SimpleCarDriver([10, 10], SideEight.track(), SimpleCar())
time_limit = 300
pop_size = 300
generations = 200


def fitness(chromosome):
    run = driver.time_trial(time_limit, chromosome)

    return driver.track_distance(run)


weights = np.random.rand(driver.nn.weights_size()) - 0.5

print("fitness test: {}".format(fitness(weights)))

pop = np.random.rand(pop_size, driver.nn.weights_size()) - 0.5


def fit(p, pf): return NumpyEvolution.single_chromosome(p, pf, fitness)


evo = NumpyEvolution(pop, 0.4, fit)

print(max(evo.fitness))

for i in range(generations):
    evo.evolve()
    if i > 10 and i % 10 == 0:
        idx = np.argpartition(evo.fitness, pop_size - 10)[-10:]
        f = [evo.fitness[a] for a in idx]
        print("generation {}, max fitness {}, mean {}".format(i, max(f), sum(f)/10))
        #r = [driver.time_trial(time_limit, evo.population[p]) for p in idx]
        #RaceWindow.new(driver.track, r, "generation "+str(i))

idx = np.argpartition(evo.fitness, pop_size - 10)[-10:]
f = [evo.fitness[a] for a in idx]
print("Final Generation, max fitness {}, mean {}".format(max(f), sum(f)/10))
r = [driver.time_trial(time_limit, evo.population[p]) for p in idx]
RaceWindow.new(driver.track, r, "Final Generation")
