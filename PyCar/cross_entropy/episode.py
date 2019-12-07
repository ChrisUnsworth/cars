import tensorflow as tf
import numpy as np


class Episode:
    def __init__(self, steps):
        self.steps = steps


class Step:
    def __init__(self, reward, action, state):
        self.reward = reward
        self.action = action
        self.state = state
