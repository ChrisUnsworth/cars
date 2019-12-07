import tensorflow as tf
import numpy as np


class FullyConnectedLayer:
    def __init__(self, in_size: int, out_size: int, activation):
        """
        Initialises a builder for a fully connected layer

        :param in_size: layer input size
        :param out_size: number of nodes
        :param activation: activation function
        """
        self.in_size = in_size
        self.out_size = out_size
        self.activation = activation

    def build_tensor(self, inputs, weights):
        w = tf.reshape(tf.slice(weights, [0], [self.in_size * self.out_size]), [self.in_size, self.out_size])
        b = tf.slice(weights, [self.in_size * self.out_size], [self.out_size])
        return self.activation(tf.matmul(tf.reshape(inputs, [1, self.in_size]), w) + b)

    def build_np(self, weights):
        w = weights[:self.in_size * self.out_size].reshape((self.out_size, self.in_size))
        b = weights[self.in_size * self.out_size:]
        def f(x): return self.activation(np.dot(w, x) + b)
        return f

    def weights_size(self):
        return self.in_size * self.out_size + self.out_size
