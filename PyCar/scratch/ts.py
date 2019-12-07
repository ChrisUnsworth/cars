import tensorflow as tf
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.tensor_network import TensorNetwork as T_Network
from network.numpy_network import NumpyNetwork as Np_Network
import numpy as np

network_t = T_Network([Layer(4, 3, tf.nn.sigmoid), Layer(3, 5, tf.nn.sigmoid)])


def np_sigmoid(a): return 1 / (1 + np.exp(-a))


network_n = Np_Network([Layer(4, 3, np_sigmoid), Layer(3, 5, np_sigmoid)])

weights = np.random.rand(network_t.weights_size())
w = tf.constant(weights, tf.float64)
x = tf.placeholder(tf.float64, shape=[network_t.in_size])
n_t = network_t.build(x, w)
n_n = network_n.build(weights)

init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init)

inputs = np.random.rand(network_t.in_size)
print(sess.run(n_t, feed_dict={x: inputs}))
print(n_n(inputs))


sess.close()
