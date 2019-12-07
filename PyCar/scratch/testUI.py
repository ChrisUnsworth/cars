#!/usr/bin/env python3
"""Test UI app

Usage:

    python3 testUI.py
"""


from PyQt5.QtWidgets import QApplication, QLabel


def show():
    app = QApplication([])
    label = QLabel('your Face!!')
    label.show()
    app.exec_()


if __name__ == '__main__':
    show()
