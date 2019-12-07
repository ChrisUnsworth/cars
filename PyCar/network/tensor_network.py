import tensorflow as tf


class TensorNetwork:
    def __init__(self, layers):
        """

        :param layers: list of network layers
        """
        self.layers = layers
        self.in_size = layers[0].in_size
        self.out_size = layers[-1].out_size

    def build(self, x, weights):
        offset = 0
        inputs = [x]
        for layer in self.layers:
            size = layer.weights_size()
            inputs.append(layer.build_tensor(inputs[-1], tf.slice(weights, [offset], [size])))
            offset += size

        return inputs[-1]

    def weights_size(self):
        return sum([l.weights_size() for l in self.layers])
