import numpy as np


class NumpyNetwork:
    def __init__(self, layers):
        """

        :param layers: list of network layers
        """
        self.layers = layers
        self.in_size = layers[0].in_size
        self.out_size = layers[-1].out_size

    def build(self, weights):
        offset = 0
        layer_functions = []
        for layer in self.layers:
            size = layer.weights_size()
            layer_functions.append(layer.build_np(weights[offset:offset + size]))
            offset += size

        def f(x):
            result = x
            for lf in layer_functions:
                result = lf(result)
            return result

        return f

    def weights_size(self):
        return sum([l.weights_size() for l in self.layers])
