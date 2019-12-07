from common.car_state import CarState


class Driver:
    def initial_state(self) -> CarState:
        pass

    def action(self, state: CarState, network) -> CarState:
        pass
