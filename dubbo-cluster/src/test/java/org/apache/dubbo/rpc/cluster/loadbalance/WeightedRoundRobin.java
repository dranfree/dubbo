package org.apache.dubbo.rpc.cluster.loadbalance;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模拟 Dubbo 的平滑加权轮询
 *
 * @author ran.ding
 * @since 2021/11/23
 */
public class WeightedRoundRobin {

    private static class Node {

        /**
         * 节点id
         */
        private final int id;

        /**
         * 节点权重
         */
        private final int weight;

        private int current;
        private int selectCnt;

        public Node(int id, int weight) {
            this.id = id;
            this.weight = weight;
            this.current = 0;
            this.selectCnt = 0;
        }

        public int id() {
            return id;
        }

        public int current() {
            return current;
        }

        public int increase() {
            this.current += weight;
            return this.current;
        }

        public int selected() {
            return selectCnt;
        }

        public void selected(int totalWeight) {
            this.current -= totalWeight;
            this.selectCnt++;
        }
    }

    public static void main(String[] args) {

        Node node1 = new Node(1, 1);
        Node node2 = new Node(2, 2);
        Node node3 = new Node(3, 3);

        List<Node> nodes = Lists.newArrayList(node1, node2, node3);

        StringBuilder print = new StringBuilder();
        for (int i = 0; i < 18; i++) {
            Node select = select(nodes);
            print.append("select: ").append(select.id).append("  ");
            print.append("current: ")
                    .append(nodes.stream()
                            .map(Node::current)
                            .map(c -> String.format("%2s", c))
                            .collect(Collectors.toList()));
            System.out.println(print);
            print.setLength(0);
        }

        System.out.println();
        System.out.println("select count: " + nodes.stream().map(Node::selected).collect(Collectors.toList()));
    }

    private static Node select(List<Node> nodes) {

        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("empty nodes!");
        }

        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        return doSelect(nodes);
    }

    private static Node doSelect(List<Node> nodes) {

        int totalWeight = 0;

        int maxCurrent = Integer.MIN_VALUE;

        Node selected = null;

        for (Node node : nodes) {
            totalWeight += node.weight;
            int cur = node.increase();
            if (cur > maxCurrent) {
                maxCurrent = cur;
                selected = node;
            }
        }

        if (selected != null) {
            selected.selected(totalWeight);
        }

        return selected;
    }
}
