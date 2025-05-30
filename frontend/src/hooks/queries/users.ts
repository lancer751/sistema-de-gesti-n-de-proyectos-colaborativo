import {getInfiniteUsers, getSingleUserById } from "@/services/users";
import { UserRequestFilters, UserRequestSort } from "@/types/user.types";
import { useInfiniteQuery, useQuery } from "@tanstack/react-query";

export function useSingleUserById(userId: number | null) {
  return useQuery({
    queryKey: ["user", {userId}],
    queryFn: () => getSingleUserById(userId!),
    enabled: !!userId,
    refetchOnWindowFocus: false
  })
}

export function useInfiniteUsers({
  filters = {},
  sorters = {},
}: {
  filters: UserRequestFilters;
  sorters: UserRequestSort;
}) {
  return useInfiniteQuery({
    queryKey: ["users", filters, sorters],
    queryFn: ({ pageParam = 0 }) =>
      getInfiniteUsers({ page: pageParam, filters, sorters }),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _allPages, lastPageParam) => {
      if (lastPage?.users.length === 0) {
        return undefined;
      }
      return ++lastPageParam;
    },
    getPreviousPageParam: (_firstPage, _allPages, firstPageParam) => {
      if (firstPageParam < 0) {
        return undefined;
      }
      return --firstPageParam;
    },
    refetchOnWindowFocus: false,
  });
}
