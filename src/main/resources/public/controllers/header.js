function Header($scope, $http) {
    $http.get('/api/band').
        success(function(data) {
            $scope.bands = data;
        });
}