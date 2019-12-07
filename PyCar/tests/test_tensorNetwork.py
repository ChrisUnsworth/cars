from unittest import TestCase
import tensorflow as tf
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.tensor_network import TensorNetwork as Network
import numpy as np


class TestTensorNetwork(TestCase):
    def test_build(self):

        layers = [Layer(4, 3, tf.nn.sigmoid), Layer(3, 5, tf.nn.sigmoid)]

        network = Network(layers)

        w = tf.placeholder(tf.float32, shape=[network.weights_size()])
        x = tf.placeholder(tf.float32, shape=[network.in_size])
        n = network.build(x, w)

        init = tf.global_variables_initializer()
        sess = tf.Session()
        sess.run(init)

        result_tensor = sess.run(n, feed_dict={x: np.random.rand(network.in_size), w: np.random.rand(network.weights_size())})
        result = list(result_tensor [0])
        sess.close()

        self.assertEqual(5, len(result))
